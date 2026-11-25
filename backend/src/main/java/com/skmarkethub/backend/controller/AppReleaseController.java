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
                "latestVersionName", "1.2.0",
                "latestVersionCode", 3,
                "releaseNotes", "🚀 SK-MarketHub v1.2.0 Update! Features Flipkart Blue/Yellow Theme, HD Product Photography, and Native SIM Card Verification. Tap 'Update Now' to update.",
                "downloadUrl", "https://github.com/skhan9897/SK-MarketHub-ApnaBazaar/raw/main/android/app/build/outputs/apk/debug/app-debug.apk",
                "sha256", "b5e2f8d1c9a4b3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8",
                "releaseDate", "2026-11-25",
                "appSize", "6.3 MB"
        ));
    }
}
