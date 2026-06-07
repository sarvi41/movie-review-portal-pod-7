package com.moviereview.portal.controller;

import com.moviereview.portal.dto.ReviewPageResponse;
import com.moviereview.portal.dto.ReviewRequest;
import com.moviereview.portal.dto.ReviewResponse;
import com.moviereview.portal.security.UserPrincipal;
import com.moviereview.portal.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Validated
@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ReviewPageResponse> listReviews(
        @PathVariable UUID movieId,
        @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
        @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") @Max(value = 50, message = "Size must be at most 50") int size
    ) {
        return ResponseEntity.ok(reviewService.listReviews(movieId, page, size));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
        @PathVariable UUID movieId,
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(movieId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
        @PathVariable UUID movieId,
        @PathVariable UUID reviewId,
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(movieId, reviewId, request, principal));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        @PathVariable UUID movieId,
        @PathVariable UUID reviewId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        reviewService.deleteReview(movieId, reviewId, principal);
        return ResponseEntity.noContent().build();
    }
}
