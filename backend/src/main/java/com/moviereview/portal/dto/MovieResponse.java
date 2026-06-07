package com.moviereview.portal.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public class MovieResponse {

    private String id;
    private String title;
    private Integer releaseYear;
    private String description;
    private String director;
    private Integer runtimeMinutes;
    private String posterUrl;
    private Double averageRating;
    private Set<String> genres;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MovieResponse() {
    }

    public MovieResponse(String id,
                         String title,
                         Integer releaseYear,
                         String description,
                         String director,
                         Integer runtimeMinutes,
                         String posterUrl,
                         Double averageRating,
                         Set<String> genres,
                         OffsetDateTime createdAt,
                         OffsetDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
        this.posterUrl = posterUrl;
        this.averageRating = averageRating;
        this.genres = genres;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
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
