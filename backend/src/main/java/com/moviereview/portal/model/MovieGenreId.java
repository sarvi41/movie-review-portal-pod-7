package com.moviereview.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovieGenreId implements Serializable {

    @Column(name = "movie_id", columnDefinition = "uuid")
    private java.util.UUID movieId;

    @Column(name = "genre_id", columnDefinition = "uuid")
    private java.util.UUID genreId;

    public MovieGenreId() {
    }

    public MovieGenreId(java.util.UUID movieId, java.util.UUID genreId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }

    public java.util.UUID getMovieId() {
        return movieId;
    }

    public java.util.UUID getGenreId() {
        return genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieGenreId that)) return false;
        return Objects.equals(movieId, that.movieId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, genreId);
    }
}
