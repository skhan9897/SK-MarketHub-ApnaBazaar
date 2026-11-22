package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.Product;
import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.entity.Wishlist;
import com.skmarkethub.backend.repository.ProductRepository;
import com.skmarkethub.backend.repository.UserRepository;
import com.skmarkethub.backend.repository.WishlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistController(
            WishlistRepository wishlistRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUser(Jwt jwt) {
        return userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping
    public List<Product> getWishlist(@AuthenticationPrincipal Jwt jwt) {
        User user = getUser(jwt);
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        List<Long> productIds = wishlists.stream().map(Wishlist::getProductId).toList();
        return productRepository.findAllById(productIds);
    }

    @PostMapping("/{productId}")
    @Transactional
    public ResponseEntity<Map<String, String>> addWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {

        User user = getUser(jwt);

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            Wishlist w = new Wishlist();
            w.setUserId(user.getId());
            w.setProductId(productId);
            wishlistRepository.save(w);
        }

        return ResponseEntity.ok(Map.of("message", "Product added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<Map<String, String>> removeWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId) {

        User user = getUser(jwt);
        Optional<Wishlist> w = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);
        w.ifPresent(wishlistRepository::delete);

        return ResponseEntity.ok(Map.of("message", "Product removed from wishlist"));
    }
}
