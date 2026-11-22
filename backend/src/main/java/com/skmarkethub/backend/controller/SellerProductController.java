package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.*;
import com.skmarkethub.backend.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/seller")
public class SellerProductController {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public SellerProductController(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository,
            InventoryRepository inventoryRepository,
            SellerRepository sellerRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.inventoryRepository = inventoryRepository;
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public record SellerProductRequest(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 100) String sku,
            @NotNull Long categoryId,
            Long brandId,
            String description,
            @Size(max = 500) String shortDescription,
            @NotNull @DecimalMin("0.00") BigDecimal basePrice,
            @DecimalMin("0.00") BigDecimal salePrice,
            @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal taxRate,
            String status,
            Boolean featured,
            @Min(0) Integer initialStock
    ) {}

    public record AddVariantRequest(
            @NotBlank @Size(max = 100) String sku,
            @NotBlank @Size(max = 150) String variantName,
            @NotNull @DecimalMin("0.00") BigDecimal price,
            @DecimalMin("0.00") BigDecimal salePrice,
            @Min(0) Integer initialStock
    ) {}

    public record AddImageRequest(
            @NotBlank @Size(max = 1000) String imageUrl,
            @Size(max = 255) String altText,
            Integer displayOrder,
            Boolean primary
    ) {}

    private Seller getSeller(Jwt jwt) {
        User user = userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller account not found or approved"));
    }

    @GetMapping("/products")
    public List<Product> getSellerProducts(@AuthenticationPrincipal Jwt jwt) {
        Seller seller = getSeller(jwt);
        return productRepository.findBySellerId(seller.getId());
    }

    @PostMapping("/products")
    @Transactional
    public ResponseEntity<Product> createSellerProduct(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerProductRequest request) {

        Seller seller = getSeller(jwt);

        if (!categoryRepository.existsById(request.categoryId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        if (request.brandId() != null && !brandRepository.existsById(request.brandId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand not found");
        }

        if (productRepository.existsBySku(request.sku().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product SKU already exists");
        }

        String slug = request.name().trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");

        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = new Product();
        product.setSellerId(seller.getId());
        product.setName(request.name().trim());
        product.setSlug(slug);
        product.setSku(request.sku().trim());
        product.setCategoryId(request.categoryId());
        product.setBrandId(request.brandId());
        product.setDescription(request.description());
        product.setShortDescription(request.shortDescription());
        product.setBasePrice(request.basePrice());
        product.setSalePrice(request.salePrice());
        product.setTaxRate(request.taxRate() == null ? BigDecimal.ZERO : request.taxRate());
        product.setStatus(request.status() == null ? "ACTIVE" : request.status().toUpperCase());
        product.setFeatured(Boolean.TRUE.equals(request.featured()));

        Product saved = productRepository.save(product);

        Inventory inv = new Inventory();
        inv.setProductId(saved.getId());
        inv.setAvailableQuantity(request.initialStock() == null ? 10 : request.initialStock());
        inv.setReservedQuantity(0);
        inventoryRepository.save(inv);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/products/{productId}/variants")
    @Transactional
    public ResponseEntity<ProductVariant> addVariant(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody AddVariantRequest request) {

        Seller seller = getSeller(jwt);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!seller.getId().equals(product.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this product");
        }

        if (productVariantRepository.existsBySku(request.sku().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Variant SKU already exists");
        }

        ProductVariant variant = new ProductVariant();
        variant.setProductId(product.getId());
        variant.setSku(request.sku().trim());
        variant.setVariantName(request.variantName().trim());
        variant.setPrice(request.price());
        variant.setSalePrice(request.salePrice());
        ProductVariant savedVariant = productVariantRepository.save(variant);

        Inventory inv = new Inventory();
        inv.setProductId(product.getId());
        inv.setVariantId(savedVariant.getId());
        inv.setAvailableQuantity(request.initialStock() == null ? 10 : request.initialStock());
        inventoryRepository.save(inv);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedVariant);
    }

    @PostMapping("/products/{productId}/images")
    @Transactional
    public ResponseEntity<ProductImage> addImage(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @Valid @RequestBody AddImageRequest request) {

        Seller seller = getSeller(jwt);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!seller.getId().equals(product.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of this product");
        }

        ProductImage img = new ProductImage();
        img.setProductId(product.getId());
        img.setImageUrl(request.imageUrl().trim());
        img.setAltText(request.altText());
        img.setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder());
        img.setPrimary(Boolean.TRUE.equals(request.primary()));

        return ResponseEntity.status(HttpStatus.CREATED).body(productImageRepository.save(img));
    }
}
