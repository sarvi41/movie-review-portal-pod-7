package com.moviereview.portal.repository;

import com.moviereview.portal.model.MovieGenre;
import com.moviereview.portal.model.MovieGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenreId> {
}
