package com.skmarkethub.backend.service;

import com.skmarkethub.backend.dto.SellerApplicationRequest;
import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public SellerService(
            SellerRepository sellerRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Map<String, Object> submitApplication(
            String email, SellerApplicationRequest request) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Inactive account cannot apply");
        }

        boolean alreadySeller = user.getRoles().stream()
                .anyMatch(role -> "SELLER".equals(role.getName()));

        if (alreadySeller) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User already has seller access");
        }

        if (sellerRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Seller application already exists");
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setStoreName(request.storeName().trim());
        seller.setBusinessName(clean(request.businessName()));
        seller.setGstNumber(clean(request.gstNumber()));
        seller.setCommissionRate(BigDecimal.ZERO);
        seller.setVerificationStatus("PENDING");
        seller.setStatus("ACTIVE");

        Seller saved = sellerRepository.save(seller);

        return sellerView(saved);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingApplications() {
        return sellerRepository
                .findByVerificationStatusOrderByCreatedAtAsc("PENDING")
                .stream()
                .map(this::sellerView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllApplications() {
        return sellerRepository.findAll()
                .stream()
                .map(this::sellerView)
                .toList();
    }

    @Transactional
    public Map<String, Object> approve(Long sellerId) {
        Seller seller = getSeller(sellerId);

        if ("APPROVED".equalsIgnoreCase(seller.getVerificationStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Application is already approved");
        }

        if (!"PENDING".equalsIgnoreCase(seller.getVerificationStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only pending applications can be approved");
        }

        Role sellerRole = roleRepository.findByName("SELLER")
                .orElseThrow(() -> new IllegalStateException(
                        "SELLER role missing from database"));

        User user = seller.getUser();
        user.getRoles().add(sellerRole);
        userRepository.save(user);

        seller.setVerificationStatus("APPROVED");
        seller.setStatus("ACTIVE");

        return sellerView(sellerRepository.save(seller));
    }

    @Transactional
    public Map<String, Object> reject(Long sellerId) {
        Seller seller = getSeller(sellerId);

        if (!"PENDING".equalsIgnoreCase(seller.getVerificationStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only pending applications can be rejected");
        }

        seller.setVerificationStatus("REJECTED");
        seller.setStatus("INACTIVE");

        return sellerView(sellerRepository.save(seller));
    }

    private Seller getSeller(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Seller application not found"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Map<String, Object> sellerView(Seller seller) {
        User user = seller.getUser();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sellerId", seller.getId());
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        result.put("mobile", user.getMobile());
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("storeName", seller.getStoreName());
        result.put("businessName", seller.getBusinessName());
        result.put("gstNumber", seller.getGstNumber());
        result.put("verificationStatus", seller.getVerificationStatus());
        result.put("status", seller.getStatus());
        result.put("createdAt", seller.getCreatedAt());
        return result;
    }
}