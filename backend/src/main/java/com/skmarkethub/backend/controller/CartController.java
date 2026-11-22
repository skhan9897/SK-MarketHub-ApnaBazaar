package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.service.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    public record AddCartItemRequest(@NotNull Long productId, Long variantId, @Min(1) int quantity) {}
    public record UpdateCartItemRequest(@Min(0) int quantity) {}

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartService.getCartSummary(jwt.getSubject()));
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddCartItemRequest request) {

        int qty = request.quantity() <= 0 ? 1 : request.quantity();
        return ResponseEntity.ok(cartService.addItem(
                jwt.getSubject(), request.productId(), request.variantId(), qty));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(cartService.updateItemQuantity(
                jwt.getSubject(), itemId, request.quantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(cartService.removeItem(jwt.getSubject(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearCart(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(cartService.clearCart(jwt.getSubject()));
    }
}
