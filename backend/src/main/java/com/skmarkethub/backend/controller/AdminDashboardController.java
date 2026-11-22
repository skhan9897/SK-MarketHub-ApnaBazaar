package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            SellerRepository sellerRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            RefundRepository refundRepository,
            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public record UserStatusRequest(@NotBlank String status) {}
    public record RefundStatusRequest(@NotBlank String status) {}

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        long totalCustomers = userRepository.count();
        long totalSellers = sellerRepository.count();
        long pendingSellers = sellerRepository.findByVerificationStatusOrderByCreatedAtAsc("PENDING").size();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<Order> orders = orderRepository.findAll();
        for (Order o : orders) {
            if ("PAID".equalsIgnoreCase(o.getPaymentStatus()) || "DELIVERED".equalsIgnoreCase(o.getOrderStatus())) {
                totalRevenue = totalRevenue.add(o.getTotalAmount());
            }
        }

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalCustomers", totalCustomers);
        metrics.put("totalSellers", totalSellers);
        metrics.put("pendingSellers", pendingSellers);
        metrics.put("totalProducts", totalProducts);
        metrics.put("totalOrders", totalOrders);
        metrics.put("totalRevenue", totalRevenue);
        return metrics;
    }

    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userRepository.findAll();
    }

    @PutMapping("/customers/{userId}/status")
    @Transactional
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UserStatusRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setStatus(request.status().toUpperCase());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/refunds")
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    @PutMapping("/refunds/{refundId}/status")
    @Transactional
    public ResponseEntity<Refund> updateRefundStatus(
            @PathVariable Long refundId,
            @RequestBody RefundStatusRequest request) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refund not found"));

        refund.setRefundStatus(request.status().toUpperCase());
        if ("APPROVED".equalsIgnoreCase(request.status())) {
            Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
            if (order != null) {
                order.setOrderStatus("REFUNDED");
                order.setPaymentStatus("REFUNDED");
                orderRepository.save(order);
            }
        }
        return ResponseEntity.ok(refundRepository.save(refund));
    }

    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
