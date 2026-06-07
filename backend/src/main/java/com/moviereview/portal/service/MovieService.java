package com.moviereview.portal.service;

import com.moviereview.portal.dto.MovieRequest;
import com.moviereview.portal.dto.MovieResponse;
import com.moviereview.portal.exception.ConflictException;
import com.moviereview.portal.exception.NotFoundException;
import com.moviereview.portal.model.Genre;
import com.moviereview.portal.model.Movie;
import com.moviereview.portal.model.MovieGenre;
import com.moviereview.portal.repository.GenreRepository;
import com.moviereview.portal.repository.MovieRepository;
import com.moviereview.portal.repository.RatingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, RatingRepository ratingRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        logger.info("Creating movie '{}' for release year {}", request.getTitle(), request.getReleaseYear());

        if (movieRepository.existsByTitleIgnoreCaseAndReleaseYear(request.getTitle(), request.getReleaseYear())) {
            throw new ConflictException("Movie with the same title and release year already exists");
        }

        Movie movie = new Movie(
            request.getTitle(),
            request.getReleaseYear(),
            request.getDescription(),
            request.getDirector(),
            request.getRuntimeMinutes(),
            request.getPosterUrl()
        );

        movie = movieRepository.save(movie);
        addGenresToMovie(movie, request.getGenres());

        return toDto(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(UUID movieId, MovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new NotFoundException("Movie not found with id: " + movieId));

        if (movieRepository.existsByTitleIgnoreCaseAndReleaseYearAndIdNot(request.getTitle(), request.getReleaseYear(), movieId)) {
            throw new ConflictException("Another movie with the same title and release year already exists");
        }

        movie.setTitle(request.getTitle());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDescription(request.getDescription());
        movie.setDirector(request.getDirector());
        movie.setRuntimeMinutes(request.getRuntimeMinutes());
        movie.setPosterUrl(request.getPosterUrl());

        if (request.getGenres() != null) {
            movie.clearGenres();
            addGenresToMovie(movie, request.getGenres());
        }

        return toDto(movieRepository.save(movie));
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(UUID movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new NotFoundException("Movie not found with id: " + movieId));
        return toDto(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> listMovies(String title, String genre) {
        title = StringUtils.hasText(title) ? title : null;
        genre = StringUtils.hasText(genre) ? genre : null;

        return movieRepository.findAllByTitleAndGenre(title, genre).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMovie(UUID movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new NotFoundException("Movie not found with id: " + movieId));

        if (!movie.getReviews().isEmpty() || !movie.getRatings().isEmpty()) {
            throw new ConflictException("Movie cannot be deleted because it has dependent reviews or ratings");
        }

        movieRepository.delete(movie);
    }

    @Transactional
    public void recalculateAverageRating(Movie movie) {
        Double averageScore = ratingRepository.findAverageScoreByMovieId(movie.getId());
        movie.setAverageRating(averageScore == null ? 0.0 : BigDecimal.valueOf(averageScore)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue());
        movieRepository.save(movie);
    }

    private void addGenresToMovie(Movie movie, Set<String> requestedGenres) {
        if (requestedGenres == null || requestedGenres.isEmpty()) {
            return;
        }

        Set<String> genreNames = requestedGenres.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(StringUtils::hasText)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String genreName : genreNames) {
            Genre genre = genreRepository.findByNameIgnoreCase(genreName)
                .orElseGet(() -> genreRepository.save(new Genre(genreName)));
            MovieGenre movieGenre = new MovieGenre(movie, genre);
            movie.addGenre(movieGenre);
            genre.addMovieGenre(movieGenre);
        }
    }

    private MovieResponse toDto(Movie movie) {
        Set<String> genres = movie.getGenres().stream()
            .map(MovieGenre::getGenre)
            .filter(Objects::nonNull)
            .map(Genre::getName)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return new MovieResponse(
            movie.getId().toString(),
            movie.getTitle(),
            movie.getReleaseYear(),
            movie.getDescription(),
            movie.getDirector(),
            movie.getRuntimeMinutes(),
            movie.getPosterUrl(),
            movie.getAverageRating(),
            genres,
            movie.getCreatedAt(),
            movie.getUpdatedAt()
        );
    }
}
