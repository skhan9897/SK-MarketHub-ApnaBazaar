package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.dto.*;
import com.skmarkethub.backend.service.AuthService;
import com.skmarkethub.backend.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    public record SendMobileOtpRequest(@jakarta.validation.constraints.NotBlank String mobile) {}
    public record MobileOtpLoginRequest(@jakarta.validation.constraints.NotBlank String mobile, @jakarta.validation.constraints.NotBlank String otpCode, String firstName) {}

    @PostMapping("/send-mobile-otp")
    public ResponseEntity<Map<String, String>> sendMobileOtp(
            @Valid @RequestBody SendMobileOtpRequest request) {
        return ResponseEntity.ok(authService.sendMobileOtp(request.mobile()));
    }

    @PostMapping("/login-mobile-otp")
    public ResponseEntity<AuthResponse> loginWithMobileOtp(
            @Valid @RequestBody MobileOtpLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithMobileOtp(request.mobile(), request.otpCode(), request.firstName()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .map(user -> {
                    Set<String> roles = user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toSet());

                    return ResponseEntity.ok(Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "mobile", user.getMobile(),
                            "roles", roles
                    ));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
