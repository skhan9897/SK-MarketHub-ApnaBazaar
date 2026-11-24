package com.skmarkethub.backend.service;

import com.skmarkethub.backend.dto.*;
import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import com.skmarkethub.backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MobileOtpRepository mobileOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecureRandom random = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            MobileOtpRepository mobileOtpRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mobileOtpRepository = mobileOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        String mobile = request.mobile() == null
                || request.mobile().isBlank()
                ? null : request.mobile().trim();

        if (mobile != null && userRepository.existsByMobile(mobile)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mobile already registered");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException(
                    "CUSTOMER role missing from database"));

        User user = new User();
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName() == null
                ? null : request.lastName().trim());
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user.setRoles(new HashSet<>(Set.of(customerRole)));

        User savedUser = userRepository.saveAndFlush(user);

        return createAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                email, request.password())
        );

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException(
                    "Invalid email or password"));

        return createAuthResponse(user);
    }

    @Transactional
    public Map<String, String> forgotPassword(ForgotPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User email not found"));

        return Map.of("message", "Password reset request initiated for " + user.getEmail());
    }

    @Transactional
    public Map<String, String> resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User email not found"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return Map.of("message", "Password reset successfully. You can now log in with your new password.");
    }

    @Transactional
    public Map<String, String> sendMobileOtp(String mobile) {
        String cleanMobile = mobile.trim().replaceAll("[^0-9+]", "");
        if (cleanMobile.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid mobile number is required");
        }

        // Generate dynamic 6-digit OTP
        String otpCode = String.format("%06d", random.nextInt(1000000));
        String hashedOtp = passwordEncoder.encode(otpCode);

        // Remove old unverified OTPs for this mobile
        mobileOtpRepository.deleteByMobile(cleanMobile);

        MobileOtp otpEntity = new MobileOtp();
        otpEntity.setMobile(cleanMobile);
        otpEntity.setOtpHash(hashedOtp);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpEntity.setAttempts(0);

        mobileOtpRepository.save(otpEntity);

        return Map.of("message", "Verification OTP sent to " + cleanMobile + " (OTP: " + otpCode + ")");
    }

    @Transactional
    public AuthResponse loginWithMobileOtp(String mobile, String otpCode, String firstName) {
        String cleanMobile = mobile.trim().replaceAll("[^0-9+]", "");

        // Validate OTP code (or master demo code 123456)
        if (!"123456".equals(otpCode)) {
            MobileOtp otpEntity = mobileOtpRepository.findTopByMobileOrderByCreatedAtDesc(cleanMobile)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not requested or expired"));

            if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new code.");
            }

            if (!passwordEncoder.matches(otpCode, otpEntity.getOtpHash())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code.");
            }

            otpEntity.setVerified(true);
            mobileOtpRepository.save(otpEntity);
        }

        // Check if user already exists with this mobile or synthetic email
        String syntheticEmail = cleanMobile + "@skmarkethub.user";
        User user = userRepository.findByMobile(cleanMobile)
                .orElseGet(() -> userRepository.findByEmailIgnoreCase(syntheticEmail)
                        .orElseGet(() -> {
                            Role customerRole = roleRepository.findByName("CUSTOMER")
                                    .orElseThrow(() -> new IllegalStateException("CUSTOMER role missing from database"));

                            User newUser = new User();
                            newUser.setEmail(syntheticEmail);
                            newUser.setMobile(cleanMobile);
                            newUser.setPasswordHash(passwordEncoder.encode("OTP_USER_NOPASS_" + System.currentTimeMillis()));
                            newUser.setFirstName((firstName != null && !firstName.isBlank()) ? firstName.trim() : "User " + cleanMobile.substring(Math.max(0, cleanMobile.length() - 4)));
                            newUser.setStatus("ACTIVE");
                            newUser.setEmailVerified(true);
                            newUser.setMobileVerified(true);
                            newUser.setRoles(new HashSet<>(Set.of(customerRole)));

                            return userRepository.saveAndFlush(newUser);
                        }));

        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(User user) {

        String token = jwtService.generateToken(user);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                roles
        );
    }
}