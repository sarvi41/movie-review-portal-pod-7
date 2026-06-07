package com.moviereview.portal.repository;

import com.moviereview.portal.model.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByMovieIdAndActiveTrue(UUID movieId, Pageable pageable);

    Optional<Review> findByIdAndMovieId(UUID id, UUID movieId);
}
