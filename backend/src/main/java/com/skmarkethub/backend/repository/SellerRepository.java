package com.skmarkethub.backend.repository;

import com.skmarkethub.backend.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByUserId(Long userId);

    List<Seller> findByVerificationStatusOrderByCreatedAtAsc(String verificationStatus);
}