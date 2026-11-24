package com.skmarkethub.backend.repository;

import com.skmarkethub.backend.entity.MobileOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileOtpRepository extends JpaRepository<MobileOtp, Long> {
    Optional<MobileOtp> findTopByMobileOrderByCreatedAtDesc(String mobile);
    void deleteByMobile(String mobile);
}
