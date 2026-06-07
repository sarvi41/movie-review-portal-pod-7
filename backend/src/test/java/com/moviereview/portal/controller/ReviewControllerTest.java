package com.moviereview.portal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.moviereview.portal.dto.ReviewPageResponse;
import com.moviereview.portal.dto.ReviewRequest;
import com.moviereview.portal.dto.ReviewResponse;
import com.moviereview.portal.service.ReviewService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewController = new ReviewController(reviewService);
    }

    @Test
    void listReviewsShouldReturnPagedResponse() {
        UUID movieId = UUID.randomUUID();
        ReviewResponse reviewResponse = new ReviewResponse(
            UUID.randomUUID().toString(),
            movieId.toString(),
            UUID.randomUUID().toString(),
            "Reviewer",
            "Great movie review text.",
            5,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        ReviewPageResponse pageResponse = new ReviewPageResponse(1, 10, 1, List.of(reviewResponse));

        when(reviewService.listReviews(movieId, 1, 10)).thenReturn(pageResponse);

        ResponseEntity<ReviewPageResponse> response = reviewController.listReviews(movieId, 1, 10);

        assertEquals(200, response.getStatusCode().value());
        ReviewPageResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.getReviews().size());
    }

    @Test
    void createReviewShouldReturnCreatedResponse() {
        UUID movieId = UUID.randomUUID();
        ReviewRequest request = new ReviewRequest();
        request.setReviewText("This is a valid review text.");
        request.setRating(5);
        ReviewResponse reviewResponse = new ReviewResponse(
            UUID.randomUUID().toString(),
            movieId.toString(),
            UUID.randomUUID().toString(),
            "Reviewer",
            request.getReviewText(),
            5,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(reviewService.createReview(any(UUID.class), any(ReviewRequest.class), any())).thenReturn(reviewResponse);

        ResponseEntity<ReviewResponse> response = reviewController.createReview(movieId, null, request);

        assertEquals(201, response.getStatusCode().value());
        ReviewResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(request.getReviewText(), responseBody.getReviewText());
    }
}
