package com.moviereview.portal.dto;

import java.util.List;

public class ReviewPageResponse {

    private int page;
    private int size;
    private long total;
    private List<ReviewResponse> reviews;

    public ReviewPageResponse() {
    }

    public ReviewPageResponse(int page, int size, long total, List<ReviewResponse> reviews) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.reviews = reviews;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
}
