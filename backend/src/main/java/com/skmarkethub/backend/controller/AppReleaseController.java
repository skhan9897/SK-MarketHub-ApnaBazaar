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
                "latestVersionName", "1.0.0",
                "latestVersionCode", 1,
                "releaseNotes", "SK-MarketHub Native Android App - Full Cross-Platform Shopping, Instant Email OTP, and Order Tracking.",
                "downloadUrl", "https://github.com/skhan9897/SK-MarketHub-ApnaBazaar/raw/main/android/app/build/outputs/apk/debug/app-debug.apk",
                "sha256", "a3c4f92d8e7b1a5c6d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c",
                "releaseDate", "2026-11-24",
                "appSize", "6.3 MB"
        ));
    }
}
