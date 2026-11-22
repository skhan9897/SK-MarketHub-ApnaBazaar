package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.dto.SellerApplicationRequest;
import com.skmarkethub.backend.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/seller-application")
    public ResponseEntity<Map<String, Object>> apply(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerApplicationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.submitApplication(
                        jwt.getSubject(), request));
    }
}