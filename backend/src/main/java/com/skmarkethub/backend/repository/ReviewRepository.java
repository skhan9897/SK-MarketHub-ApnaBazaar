package com.skmarkethub.backend.repository;

import com.skmarkethub.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndStatus(Long productId, String status);
    List<Review> findByUserId(Long userId);
}
