package com.moviereview.portal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.moviereview.portal.dto.MovieRequest;
import com.moviereview.portal.dto.MovieResponse;
import com.moviereview.portal.exception.ConflictException;
import com.moviereview.portal.exception.NotFoundException;
import com.moviereview.portal.model.Genre;
import com.moviereview.portal.model.Movie;
import com.moviereview.portal.repository.GenreRepository;
import com.moviereview.portal.repository.MovieRepository;
import com.moviereview.portal.repository.RatingRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private RatingRepository ratingRepository;

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieService(movieRepository, genreRepository, ratingRepository);
    }

    @Test
    void createMovieShouldSaveMovieWithGenres() {
        UUID movieId = UUID.randomUUID();
        MovieRequest request = new MovieRequest();
        request.setTitle("Example Movie");
        request.setReleaseYear(2024);
        request.setDescription("A new movie example.");
        request.setDirector("Acme Director");
        request.setRuntimeMinutes(120);
        request.setPosterUrl("http://example.com/poster.jpg");
        request.setGenres(Set.of("Action", "Drama"));

        when(movieRepository.existsByTitleIgnoreCaseAndReleaseYear(request.getTitle(), request.getReleaseYear()))
            .thenReturn(false);

        when(movieRepository.save(org.mockito.ArgumentMatchers.<Movie>any())).thenAnswer(invocation -> {
            Movie saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                setField(saved, "id", movieId);
            }
            return saved;
        });

        when(genreRepository.findByNameIgnoreCase(org.mockito.ArgumentMatchers.<String>any())).thenReturn(Optional.empty());
        when(genreRepository.save(org.mockito.ArgumentMatchers.<Genre>any())).thenAnswer(invocation -> {
            Genre genre = invocation.getArgument(0);
            setField(genre, "id", UUID.randomUUID());
            return genre;
        });

        MovieResponse response = movieService.createMovie(request);

        assertNotNull(response);
        assertEquals(movieId.toString(), response.getId());
        assertEquals("Example Movie", response.getTitle());
        assertEquals(2, response.getGenres().size());
    }

    @Test
    void createMovieShouldThrowConflictWhenDuplicateExists() {
        MovieRequest request = new MovieRequest();
        request.setTitle("Example Movie");
        request.setReleaseYear(2024);
        request.setDescription("A new movie example.");
        request.setDirector("Acme Director");
        request.setRuntimeMinutes(120);

        when(movieRepository.existsByTitleIgnoreCaseAndReleaseYear(request.getTitle(), request.getReleaseYear()))
            .thenReturn(true);

        assertThrows(ConflictException.class, () -> movieService.createMovie(request));
    }

    @Test
    void recalculateAverageRatingShouldSaveRoundedMovieValue() {
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie("Example Movie", 2024, "Description", "Director", 120, "http://example.com/poster.jpg");
        setField(movie, "id", movieId);

        when(ratingRepository.findAverageScoreByMovieId(movieId)).thenReturn(4.3333333);
        when(movieRepository.save(org.mockito.ArgumentMatchers.<Movie>any())).thenAnswer(invocation -> invocation.getArgument(0));

        movieService.recalculateAverageRating(movie);

        assertEquals(4.33, movie.getAverageRating());
    }

    @Test
    void getMovieByIdShouldThrowNotFoundWhenMissing() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.<Movie>empty());

        assertThrows(NotFoundException.class, () -> movieService.getMovieById(movieId));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException("Unable to set field value", ex);
        }
    }
}
