package com.moviereview.portal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moviereview.portal.dto.ReviewPageResponse;
import com.moviereview.portal.dto.ReviewRequest;
import com.moviereview.portal.dto.ReviewResponse;
import com.moviereview.portal.exception.NotFoundException;
import com.moviereview.portal.model.Movie;
import com.moviereview.portal.model.Review;
import com.moviereview.portal.model.User;
import com.moviereview.portal.model.UserStatus;
import com.moviereview.portal.model.UserRole;
import com.moviereview.portal.model.Role;
import com.moviereview.portal.repository.AuditLogRepository;
import com.moviereview.portal.repository.MovieRepository;
import com.moviereview.portal.repository.RatingRepository;
import com.moviereview.portal.repository.ReviewRepository;
import com.moviereview.portal.repository.UserRepository;
import com.moviereview.portal.security.UserPrincipal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.PageImpl;

@SuppressWarnings("null")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MovieService movieService;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository, movieRepository, userRepository, auditLogRepository, ratingRepository, movieService);
    }

    @Test
    void createReviewShouldPersistReviewAndReturnResponse() {
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        Movie movie = new Movie();
        movie.setTitle("Example");
        movie.setReleaseYear(2024);
        movie.setDescription("Description");
        movie.setDirector("Director");
        movie.setRuntimeMinutes(120);
        movie.setPosterUrl("http://example.com/poster.jpg");
        java.util.UUID id = movieId;
        try {
            java.lang.reflect.Field field = Movie.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(movie, id);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        User user = new User("user@example.com", "hash", "User", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, userId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        ReviewRequest request = new ReviewRequest();
        request.setReviewText("This is a valid review text.");
        request.setRating(5);
        user.addRole(new UserRole(user, Role.USER));
        UserPrincipal principal = UserPrincipal.create(user);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ratingRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());
        when(reviewRepository.save(org.mockito.Mockito.<Review>any())).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            try {
                java.lang.reflect.Field field = Review.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(review, reviewId);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
            return review;
        });

        ReviewResponse response = reviewService.createReview(movieId, request, principal);

        assertEquals(reviewId.toString(), response.getId());
        assertEquals(movieId.toString(), response.getMovieId());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals("This is a valid review text.", response.getReviewText());
        verify(movieService).recalculateAverageRating(movie);
        verify(auditLogRepository).save(org.mockito.Mockito.<com.moviereview.portal.model.AuditLog>any());
    }

    @Test
    void updateReviewShouldModifyOwnedReview() {
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = new User("user@example.com", "hash", "User", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, userId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Movie movie = new Movie();
        try {
            java.lang.reflect.Field field = Movie.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(movie, movieId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Review review = new Review(user, movie, "Initial review text.");
        try {
            java.lang.reflect.Field field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, reviewId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        ReviewRequest request = new ReviewRequest();
        request.setReviewText("Updated review text with sufficient length.");
        request.setRating(4);
        user.addRole(new UserRole(user, Role.USER));
        UserPrincipal principal = UserPrincipal.create(user);

        when(reviewRepository.findByIdAndMovieId(reviewId, movieId)).thenReturn(Optional.of(review));
        when(ratingRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());
        when(reviewRepository.save(org.mockito.Mockito.<Review>any())).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.updateReview(movieId, reviewId, request, principal);

        assertEquals(reviewId.toString(), response.getId());
        assertEquals("Updated review text with sufficient length.", response.getReviewText());
        verify(movieService).recalculateAverageRating(review.getMovie());
        verify(auditLogRepository).save(org.mockito.Mockito.<com.moviereview.portal.model.AuditLog>any());
    }

    @Test
    void updateReviewShouldThrowWhenUnauthorized() {
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User owner = new User("owner@example.com", "hash", "Owner", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(owner, userId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Movie movie = new Movie();
        try {
            java.lang.reflect.Field field = Movie.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(movie, movieId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Review review = new Review(owner, movie, "Owned review text.");
        try {
            java.lang.reflect.Field field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, reviewId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        User other = new User("other@example.com", "hash", "Other", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(other, otherUserId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        other.addRole(new UserRole(other, Role.USER));
        UserPrincipal principal = UserPrincipal.create(other);
        ReviewRequest request = new ReviewRequest();
        request.setReviewText("Attempted update text.");
        request.setRating(3);

        when(reviewRepository.findByIdAndMovieId(reviewId, movieId)).thenReturn(Optional.of(review));

        assertThrows(AccessDeniedException.class, () -> reviewService.updateReview(movieId, reviewId, request, principal));
    }

    @Test
    void deleteReviewShouldRecalculateAverageRating() {
        UUID movieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = new User("user@example.com", "hash", "User", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, userId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Movie movie = new Movie();
        try {
            java.lang.reflect.Field field = Movie.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(movie, movieId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Review review = new Review(user, movie, "Review to delete.");
        try {
            java.lang.reflect.Field field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, reviewId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        UserPrincipal principal = UserPrincipal.create(user);

        when(reviewRepository.findByIdAndMovieId(reviewId, movieId)).thenReturn(Optional.of(review));
        when(ratingRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());

        reviewService.deleteReview(movieId, reviewId, principal);

        verify(reviewRepository).delete(review);
        verify(movieService).recalculateAverageRating(movie);
    }

    @Test
    void listReviewsShouldReturnPagedResponse() {
        UUID movieId = UUID.randomUUID();
        User user = new User("user@example.com", "hash", "User", UserStatus.ACTIVE);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, UUID.randomUUID());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Movie movie = new Movie();
        try {
            java.lang.reflect.Field field = Movie.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(movie, movieId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        Review review = new Review(user, movie, "A review text example.");
        try {
            java.lang.reflect.Field field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(review, UUID.randomUUID());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(ratingRepository.findByUserIdAndMovieId(user.getId(), movieId)).thenReturn(Optional.empty());
        when(reviewRepository.findByMovieIdAndActiveTrue(movieId, org.mockito.Mockito.<org.springframework.data.domain.Pageable>any()))
            .thenReturn(new PageImpl<>(List.of(review)));

        ReviewPageResponse pageResponse = reviewService.listReviews(movieId, 1, 10);

        assertEquals(1, pageResponse.getPage());
        assertEquals(10, pageResponse.getSize());
        assertEquals(1, pageResponse.getTotal());
        assertEquals(1, pageResponse.getReviews().size());
    }

    @Test
    void listReviewsShouldThrowWhenMovieMissing() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.existsById(movieId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewService.listReviews(movieId, 1, 10));
    }
}
