package com.skmarkethub.backend.service;

import com.skmarkethub.backend.entity.EmailOtp;
import com.skmarkethub.backend.kafka.EmailNotificationProducer;
import com.skmarkethub.backend.repository.EmailOtpRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmailOtpService {

    private final EmailOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationProducer producer;
    private final EmailProviderService emailProviderService;
    private final SecureRandom random = new SecureRandom();

    public EmailOtpService(
            EmailOtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            EmailNotificationProducer producer,
            EmailProviderService emailProviderService) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.producer = producer;
        this.emailProviderService = emailProviderService;
    }

    @Transactional
    public Map<String, String> sendOtp(String email) {
        String cleanEmail = email.trim().toLowerCase();

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", random.nextInt(1000000));
        String hashedOtp = passwordEncoder.encode(otpCode);

        // Delete existing unverified OTPs for this email
        otpRepository.deleteByEmail(cleanEmail);

        EmailOtp otpEntity = new EmailOtp();
        otpEntity.setEmail(cleanEmail);
        otpEntity.setOtpHash(hashedOtp);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpEntity.setAttempts(0);

        otpRepository.save(otpEntity);

        // Publish to Kafka & Dispatch Email
        producer.sendOtpRequestedEvent(cleanEmail, otpCode);
        emailProviderService.sendOtpHtmlEmail(cleanEmail, otpCode);

        return Map.of("message", "Verification OTP sent to " + cleanEmail);
    }

    @Transactional
    public Map<String, String> verifyOtp(String email, String otpCode) {
        String cleanEmail = email.trim().toLowerCase();

        EmailOtp otpEntity = otpRepository.findTopByEmailOrderByCreatedAtDesc(cleanEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not requested or expired"));

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new code.");
        }

        if (otpEntity.getAttempts() >= 5) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Maximum OTP attempts exceeded.");
        }

        otpEntity.setAttempts(otpEntity.getAttempts() + 1);

        if (!passwordEncoder.matches(otpCode, otpEntity.getOtpHash())) {
            otpRepository.save(otpEntity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code.");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return Map.of("message", "Email verified successfully.");
    }
}
