package com.moviereview.portal.repository;

import com.moviereview.portal.model.Rating;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.movie.id = :movieId")
    Double findAverageScoreByMovieId(@Param("movieId") UUID movieId);

    Optional<Rating> findByUserIdAndMovieId(UUID userId, UUID movieId);
}
