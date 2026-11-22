package com.sk.markethub.model;

import java.util.Set;

public class AuthResponse {
    private String accessToken;
    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String firstName;
    private Set<String> roles;

    public AuthResponse() {}

    public String getToken() { return accessToken != null ? accessToken : token; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public Set<String> getRoles() { return roles; }
}
