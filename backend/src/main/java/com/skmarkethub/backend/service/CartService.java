package com.skmarkethub.backend.service;

import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCartSummary(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = getOrCreateCart(user.getId());
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<Map<String, Object>> itemDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) continue;

            ProductVariant variant = item.getVariantId() != null
                    ? productVariantRepository.findById(item.getVariantId()).orElse(null) : null;

            BigDecimal unitPrice = (variant != null && variant.getSalePrice() != null) ? variant.getSalePrice()
                    : (variant != null && variant.getPrice() != null) ? variant.getPrice()
                    : (product.getSalePrice() != null) ? product.getSalePrice()
                    : product.getBasePrice();

            BigDecimal totalItemPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(totalItemPrice);

            String primaryImage = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId())
                    .stream().findFirst().map(ProductImage::getImageUrl).orElse(null);

            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("itemId", item.getId());
            detail.put("productId", product.getId());
            detail.put("productName", product.getName());
            detail.put("sku", variant != null ? variant.getSku() : product.getSku());
            detail.put("variantId", item.getVariantId());
            detail.put("variantName", variant != null ? variant.getVariantName() : null);
            detail.put("imageUrl", primaryImage);
            detail.put("unitPrice", unitPrice);
            detail.put("quantity", item.getQuantity());
            detail.put("totalPrice", totalItemPrice);

            itemDetails.add(detail);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cartId", cart.getId());
        result.put("items", itemDetails);
        result.put("subtotal", subtotal);
        result.put("itemCount", items.stream().mapToInt(CartItem::getQuantity).sum());
        return result;
    }

    @Transactional
    public Map<String, Object> addItem(String email, Long productId, Long variantId, int quantity) {
        if (quantity <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be > 0");

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        Cart cart = getOrCreateCart(user.getId());
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), productId, variantId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setProductId(productId);
            item.setVariantId(variantId);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCartSummary(email);
    }

    @Transactional
    public Map<String, Object> updateItemQuantity(String email, Long itemId, int quantity) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = getOrCreateCart(user.getId());
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (!item.getCartId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this cart item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCartSummary(email);
    }

    @Transactional
    public Map<String, Object> removeItem(String email, Long itemId) {
        return updateItemQuantity(email, itemId, 0);
    }

    @Transactional
    public Map<String, Object> clearCart(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = getOrCreateCart(user.getId());
        cartItemRepository.deleteByCartId(cart.getId());
        return getCartSummary(email);
    }
}
