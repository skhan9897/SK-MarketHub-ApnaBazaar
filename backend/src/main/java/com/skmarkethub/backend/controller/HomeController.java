package com.skmarkethub.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "app", "SK-MarketHub API",
                "message", "SK-MarketHub Production API is Live and Operational!",
                "tagline", "SHOP SMART. LIVE BETTER."
        ));
    }
}
