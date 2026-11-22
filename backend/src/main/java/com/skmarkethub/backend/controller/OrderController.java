package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.Review;
import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.repository.ProductRepository;
import com.skmarkethub.backend.repository.ReviewRepository;
import com.skmarkethub.backend.repository.UserRepository;
import com.skmarkethub.backend.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderController(
            OrderService orderService,
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderService = orderService;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public record CheckoutRequest(@NotNull Long addressId, String couponCode) {}
    public record PaymentRequest(String paymentMethod) {}
    public record RefundRequest(String reason) {}
    public record ReviewRequest(
            @NotNull Long productId,
            Long orderItemId,
            @NotNull @Min(1) @Max(5) Integer rating,
            String title,
            String text
    ) {}

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CheckoutRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(jwt.getSubject(), request.addressId(), request.couponCode()));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUserOrders(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderService.getUserOrders(jwt.getSubject()));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(orderService.processPayment(jwt.getSubject(), orderId, request.paymentMethod()));
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long orderId,
            @RequestBody RefundRequest request) {

        return ResponseEntity.ok(orderService.requestRefund(jwt.getSubject(), orderId, request.reason()));
    }

    @PostMapping("/reviews")
    @Transactional
    public ResponseEntity<Review> createReview(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReviewRequest request) {

        User user = userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!productRepository.existsById(request.productId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        Review review = new Review();
        review.setUserId(user.getId());
        review.setProductId(request.productId());
        review.setOrderItemId(request.orderItemId());
        review.setRating(request.rating());
        review.setReviewTitle(request.title() == null ? null : request.title().trim());
        review.setReviewText(request.text() == null ? null : request.text().trim());
        review.setVerifiedPurchase(request.orderItemId() != null);
        review.setStatus("APPROVED");

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewRepository.save(review));
    }
}
