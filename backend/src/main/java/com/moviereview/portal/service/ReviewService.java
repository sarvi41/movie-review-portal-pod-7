package com.moviereview.portal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.portal.dto.ReviewPageResponse;
import com.moviereview.portal.dto.ReviewRequest;
import com.moviereview.portal.dto.ReviewResponse;
import com.moviereview.portal.exception.NotFoundException;
import com.moviereview.portal.model.AuditLog;
import com.moviereview.portal.model.Movie;
import com.moviereview.portal.model.Rating;
import com.moviereview.portal.model.Review;
import com.moviereview.portal.model.User;
import com.moviereview.portal.repository.AuditLogRepository;
import com.moviereview.portal.repository.MovieRepository;
import com.moviereview.portal.repository.RatingRepository;
import com.moviereview.portal.repository.ReviewRepository;
import com.moviereview.portal.repository.UserRepository;
import com.moviereview.portal.security.UserPrincipal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final RatingRepository ratingRepository;
    private final MovieService movieService;

    public ReviewService(ReviewRepository reviewRepository,
                         MovieRepository movieRepository,
                         UserRepository userRepository,
                         AuditLogRepository auditLogRepository,
                         RatingRepository ratingRepository,
                         MovieService movieService) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.ratingRepository = ratingRepository;
        this.movieService = movieService;
    }

    @Transactional
    public ReviewResponse createReview(UUID movieId, ReviewRequest request, UserPrincipal principal) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new NotFoundException("Movie not found with id: " + movieId));

        User actor = userRepository.findById(principal.getId())
            .orElseThrow(() -> new NotFoundException("User not found with id: " + principal.getId()));

        Review review = new Review(actor, movie, request.getReviewText());
        review = reviewRepository.save(review);
        saveOrUpdateRating(actor, movie, request.getRating());
        movieService.recalculateAverageRating(movie);
        auditLogRepository.save(new AuditLog(actor, "Review", review.getId(), "CREATE", serializeDetails(Map.of(
            "movieId", movieId.toString(),
            "rating", request.getRating(),
            "reviewText", request.getReviewText()
        ))));

        return toDto(review);
    }

    @Transactional
    public ReviewResponse updateReview(UUID movieId, UUID reviewId, ReviewRequest request, UserPrincipal principal) {
        Review review = reviewRepository.findByIdAndMovieId(reviewId, movieId)
            .orElseThrow(() -> new NotFoundException("Review not found for movie id: " + movieId));

        assertPermission(principal, review);
        String previousText = review.getReviewText();
        review.setReviewText(request.getReviewText());
        saveOrUpdateRating(getActor(principal), review.getMovie(), request.getRating());

        review = reviewRepository.save(review);
        movieService.recalculateAverageRating(review.getMovie());
        auditLogRepository.save(new AuditLog(getActor(principal), "Review", review.getId(), "UPDATE", serializeDetails(Map.of(
            "movieId", movieId.toString(),
            "rating", request.getRating(),
            "previousReviewText", previousText,
            "updatedReviewText", request.getReviewText()
        ))));

        return toDto(review);
    }

    @Transactional
    public void deleteReview(UUID movieId, UUID reviewId, UserPrincipal principal) {
        Review review = reviewRepository.findByIdAndMovieId(reviewId, movieId)
            .orElseThrow(() -> new NotFoundException("Review not found for movie id: " + movieId));

        assertPermission(principal, review);
        Movie movie = review.getMovie();
        ratingRepository.findByUserIdAndMovieId(review.getUser().getId(), movie.getId()).ifPresent(ratingRepository::delete);
        reviewRepository.delete(review);
        movieService.recalculateAverageRating(movie);
        auditLogRepository.save(new AuditLog(getActor(principal), "Review", review.getId(), "DELETE", serializeDetails(Map.of(
            "movieId", movieId.toString(),
            "reviewText", review.getReviewText()
        ))));
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse listReviews(UUID movieId, int page, int size) {
        if (!movieRepository.existsById(movieId)) {
            throw new NotFoundException("Movie not found with id: " + movieId);
        }

        Page<Review> reviewPage = reviewRepository.findByMovieIdAndActiveTrue(movieId, PageRequest.of(page - 1, size));
        List<ReviewResponse> reviews = reviewPage.getContent().stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return new ReviewPageResponse(page, size, reviewPage.getTotalElements(), reviews);
    }

    private Rating saveOrUpdateRating(User user, Movie movie, Integer score) {
        return ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId())
            .map(existingRating -> {
                existingRating.setScore(score);
                return ratingRepository.save(existingRating);
            })
            .orElseGet(() -> ratingRepository.save(new Rating(user, movie, score)));
    }

    private void assertPermission(UserPrincipal principal, Review review) {
        boolean isOwner = review.getUser() != null && review.getUser().getId() != null && review.getUser().getId().equals(principal.getId());
        boolean isAdmin = principal.getRoles().contains("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("User is not authorized to modify this review");
        }
    }

    private User getActor(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new NotFoundException("User not found with id: " + principal.getId()));
    }

    private String serializeDetails(Map<String, Object> details) {
        try {
            return OBJECT_MAPPER.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize audit log details", ex);
        }
    }

    private ReviewResponse toDto(Review review) {
        Integer rating = ratingRepository.findByUserIdAndMovieId(review.getUser().getId(), review.getMovie().getId())
            .map(Rating::getScore)
            .orElse(null);

        return new ReviewResponse(
            review.getId().toString(),
            review.getMovie().getId().toString(),
            review.getUser().getId().toString(),
            review.getUser().getName(),
            review.getReviewText(),
            rating,
            review.getCreatedAt(),
            review.getUpdatedAt()
        );
    }
}
