package com.moviereview.portal.repository;

import com.moviereview.portal.model.Movie;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

    boolean existsByTitleIgnoreCaseAndReleaseYear(String title, Integer releaseYear);

    boolean existsByTitleIgnoreCaseAndReleaseYearAndIdNot(String title, Integer releaseYear, UUID id);

    @Query("""
        SELECT DISTINCT m
        FROM Movie m
        LEFT JOIN m.genres mg
        LEFT JOIN mg.genre g
        WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:genre IS NULL OR LOWER(g.name) = LOWER(:genre))
    """)
    List<Movie> findAllByTitleAndGenre(@Param("title") String title, @Param("genre") String genre);
}
