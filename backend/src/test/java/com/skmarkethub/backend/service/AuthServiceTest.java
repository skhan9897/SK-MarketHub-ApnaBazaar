package com.skmarkethub.backend.service;

import com.skmarkethub.backend.dto.*;
import com.skmarkethub.backend.entity.Role;
import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.repository.RoleRepository;
import com.skmarkethub.backend.repository.UserRepository;
import com.skmarkethub.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Role customerRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        customerRole = new Role();
        customerRole.setId(1L);
        customerRole.setName("CUSTOMER");

        testUser = new User();
        testUser.setId(10L);
        testUser.setEmail("customer@skmarkethub.com");
        testUser.setFirstName("John");
        testUser.setPasswordHash("hashed_pass");
        testUser.setRoles(new HashSet<>(Set.of(customerRole)));
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("customer@skmarkethub.com", "9876543210", "password123", "John", "Doe");

        when(userRepository.existsByEmailIgnoreCase("customer@skmarkethub.com")).thenReturn(false);
        when(userRepository.existsByMobile("9876543210")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pass");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.accessToken());
        assertEquals("customer@skmarkethub.com", response.email());
        assertTrue(response.roles().contains("CUSTOMER"));
    }

    @Test
    void register_DuplicateEmail_ThrowsConflict() {
        RegisterRequest request = new RegisterRequest("customer@skmarkethub.com", "9876543210", "password123", "John", "Doe");

        when(userRepository.existsByEmailIgnoreCase("customer@skmarkethub.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.register(request));
    }

    @Test
    void forgotPassword_Success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("customer@skmarkethub.com");
        when(userRepository.findByEmailIgnoreCase("customer@skmarkethub.com")).thenReturn(Optional.of(testUser));

        Map<String, String> res = authService.forgotPassword(request);

        assertNotNull(res);
        assertTrue(res.get("message").contains("initiated"));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("customer@skmarkethub.com", "password123");

        when(userRepository.findByEmailIgnoreCase("customer@skmarkethub.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.accessToken());
        assertEquals("customer@skmarkethub.com", response.email());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void resetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest("customer@skmarkethub.com", "newpassword123");
        when(userRepository.findByEmailIgnoreCase("customer@skmarkethub.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("new_hashed_pass");

        Map<String, String> res = authService.resetPassword(request);

        assertNotNull(res);
        assertEquals("new_hashed_pass", testUser.getPasswordHash());
        verify(userRepository).save(testUser);
    }
}
