package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.service.EmailOtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OtpController {

    private final EmailOtpService otpService;

    public OtpController(EmailOtpService otpService) {
        this.otpService = otpService;
    }

    public record SendOtpRequest(@NotBlank @Email String email) {}
    public record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank String otpCode) {}

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(otpService.sendOtp(request.email()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(otpService.verifyOtp(request.email(), request.otpCode()));
    }
}
