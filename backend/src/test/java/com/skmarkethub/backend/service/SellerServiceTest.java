package com.skmarkethub.backend.service;

import com.skmarkethub.backend.dto.SellerApplicationRequest;
import com.skmarkethub.backend.entity.Role;
import com.skmarkethub.backend.entity.Seller;
import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.repository.RoleRepository;
import com.skmarkethub.backend.repository.SellerRepository;
import com.skmarkethub.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private SellerService sellerService;

    private User testUser;
    private Role customerRole;
    private Role sellerRole;

    @BeforeEach
    void setUp() {
        customerRole = new Role();
        customerRole.setId(1L);
        customerRole.setName("CUSTOMER");

        sellerRole = new Role();
        sellerRole.setId(3L);
        sellerRole.setName("SELLER");

        testUser = new User();
        testUser.setId(4L);
        testUser.setEmail("seller@skmarkethub.com");
        testUser.setFirstName("SK");
        testUser.setLastName("Seller");
        testUser.setStatus("ACTIVE");
        testUser.setRoles(new HashSet<>(Set.of(customerRole)));
    }

    @Test
    void submitApplication_Success() {
        SellerApplicationRequest request = new SellerApplicationRequest(
                "SK Official Store", "SK MarketHub", "29ABCDE1234F1Z5");

        when(userRepository.findByEmailIgnoreCase("seller@skmarkethub.com"))
                .thenReturn(Optional.of(testUser));
        when(sellerRepository.findByUserId(4L)).thenReturn(Optional.empty());
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Map<String, Object> response = sellerService.submitApplication("seller@skmarkethub.com", request);

        assertNotNull(response);
        assertEquals(1L, response.get("sellerId"));
        assertEquals("SK Official Store", response.get("storeName"));
        assertEquals("PENDING", response.get("verificationStatus"));
    }

    @Test
    void submitApplication_AlreadySeller_ThrowsConflict() {
        testUser.getRoles().add(sellerRole);
        SellerApplicationRequest request = new SellerApplicationRequest(
                "SK Official Store", "SK MarketHub", "29ABCDE1234F1Z5");

        when(userRepository.findByEmailIgnoreCase("seller@skmarkethub.com"))
                .thenReturn(Optional.of(testUser));

        assertThrows(ResponseStatusException.class, () ->
                sellerService.submitApplication("seller@skmarkethub.com", request));
    }

    @Test
    void approve_Success_AssignsSellerRole() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setUser(testUser);
        seller.setStoreName("SK Official Store");
        seller.setVerificationStatus("PENDING");
        seller.setStatus("ACTIVE");

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Map<String, Object> result = sellerService.approve(1L);

        assertEquals("APPROVED", seller.getVerificationStatus());
        assertTrue(testUser.getRoles().stream().anyMatch(r -> "SELLER".equals(r.getName())));
    }
}
