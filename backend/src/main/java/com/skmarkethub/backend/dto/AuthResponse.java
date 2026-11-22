package com.skmarkethub.backend.dto;

import java.util.Set;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long userId,
    String email,
    String firstName,
    Set<String> roles
) {}
