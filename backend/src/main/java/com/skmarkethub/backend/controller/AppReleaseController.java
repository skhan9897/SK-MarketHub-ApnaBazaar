package com.skmarkethub.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/app")
public class AppReleaseController {

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestRelease() {
        return ResponseEntity.ok(Map.of(
                "latestVersionName", "1.1.0",
                "latestVersionCode", 2,
                "releaseNotes", "🚀 SK-MarketHub v1.1.0 Released! Features Native Device SIM Verification, Passwordless Mobile OTP Login, and Performance Upgrades. Tap 'Update Now' to update.",
                "downloadUrl", "https://sk-markethub-apnabazaar.onrender.com/download-app",
                "sha256", "b5e2f8d1c9a4b3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8",
                "releaseDate", "2026-11-24",
                "appSize", "6.3 MB"
        ));
    }
}
