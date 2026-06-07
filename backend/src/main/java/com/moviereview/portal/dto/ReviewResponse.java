package com.moviereview.portal.dto;

import java.time.OffsetDateTime;

public class ReviewResponse {

    private String id;
    private String movieId;
    private String userId;
    private String userName;
    private String reviewText;
    private Integer rating;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ReviewResponse() {
    }

    public ReviewResponse(String id,
                          String movieId,
                          String userId,
                          String userName,
                          String reviewText,
                          Integer rating,
                          OffsetDateTime createdAt,
                          OffsetDateTime updatedAt) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.userName = userName;
        this.reviewText = reviewText;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
