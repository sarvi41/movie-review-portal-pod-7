package com.moviereview.portal.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "movies", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "release_year"}))
@Check(constraints = "release_year >= 1888 AND release_year <= 2100 AND runtime_minutes > 0")
public class Movie {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private java.util.UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false, length = 200)
    private String director;

    @Column(name = "runtime_minutes", nullable = false)
    private Integer runtimeMinutes;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieGenre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private Set<Rating> ratings = new HashSet<>();

    public Movie() {
    }

    public Movie(String title, Integer releaseYear, String description, String director, Integer runtimeMinutes, String posterUrl) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.director = director;
        this.runtimeMinutes = runtimeMinutes;
        this.posterUrl = posterUrl;
    }

    public java.util.UUID getId() {
        return id;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Set<MovieGenre> getGenres() {
        return genres;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void addGenre(MovieGenre movieGenre) {
        genres.add(movieGenre);
        if (movieGenre.getGenre() != null) {
            movieGenre.getGenre().addMovieGenre(movieGenre);
        }
    }

    public void removeGenre(MovieGenre movieGenre) {
        genres.remove(movieGenre);
        if (movieGenre.getGenre() != null) {
            movieGenre.getGenre().getMovieGenres().remove(movieGenre);
        }
    }

    public void clearGenres() {
        for (MovieGenre movieGenre : new HashSet<>(genres)) {
            removeGenre(movieGenre);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
