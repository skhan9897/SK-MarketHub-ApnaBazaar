package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.Brand;
import com.skmarkethub.backend.entity.Category;
import com.skmarkethub.backend.entity.Product;
import com.skmarkethub.backend.entity.ProductImage;
import com.skmarkethub.backend.entity.ProductVariant;
import com.skmarkethub.backend.entity.Inventory;
import com.skmarkethub.backend.repository.BrandRepository;
import com.skmarkethub.backend.repository.CategoryRepository;
import com.skmarkethub.backend.repository.ProductRepository;
import com.skmarkethub.backend.repository.ProductImageRepository;
import com.skmarkethub.backend.repository.ProductVariantRepository;
import com.skmarkethub.backend.repository.InventoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogController(
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public record CategoryRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 180) String slug,
            Long parentId,
            String description,
            @Size(max = 500) String imageUrl) {}

    public record BrandRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 180) String slug,
            @Size(max = 500) String logoUrl) {}

    public record ProductRequest(
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
            Boolean featured) {}

    private String makeSlug(String value) {
        String slug = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (slug.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A valid name is required to create a slug");
        }
        return slug;
    }

    private void validateSlug(String slug, int maxLength) {
        if (slug.length() > maxLength) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Slug exceeds maximum length");
        }
    }

    private String normalizeStatus(String status) {
        String value = (status == null || status.isBlank())
                ? "DRAFT" : status.trim().toUpperCase(Locale.ROOT);

        if (!Set.of("DRAFT", "ACTIVE", "INACTIVE").contains(value)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status must be DRAFT, ACTIVE or INACTIVE");
        }
        return value;
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findByActiveTrue();
    }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category not found");
        }
        return category;
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Category createCategory(@Valid @RequestBody CategoryRequest request) {
        String slug = makeSlug(
                request.slug() == null || request.slug().isBlank()
                        ? request.name() : request.slug());

        validateSlug(slug, 180);

        if (categoryRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Category slug already exists");
        }

        if (request.parentId() != null &&
                !categoryRepository.existsById(request.parentId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Parent category not found");
        }

        Category category = new Category();
        category.setName(request.name().trim());
        category.setSlug(slug);
        category.setParentId(request.parentId());
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setActive(true);

        return categoryRepository.save(category);
    }

    @GetMapping("/brands")
    public List<Brand> getBrands() {
        return brandRepository.findByActiveTrue();
    }

    @GetMapping("/brands/{id}")
    public Brand getBrand(@PathVariable Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Brand not found"));

        if (!Boolean.TRUE.equals(brand.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Brand not found");
        }
        return brand;
    }

    @PostMapping("/admin/brands")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Brand createBrand(@Valid @RequestBody BrandRequest request) {
        String name = request.name().trim();
        String slug = makeSlug(
                request.slug() == null || request.slug().isBlank()
                        ? name : request.slug());

        validateSlug(slug, 180);

        boolean duplicateName = brandRepository.findAll().stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(name));

        if (duplicateName || brandRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Brand name or slug already exists");
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        brand.setLogoUrl(request.logoUrl());
        brand.setActive(true);

        return brandRepository.save(brand);
    }

    @GetMapping("/products")
    public List<Product> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return productRepository.findByNameContainingIgnoreCaseAndStatus(search.trim(), "ACTIVE");
        }

        if (categoryId != null && brandId != null) {
            return productRepository.findByCategoryIdAndStatus(categoryId, "ACTIVE")
                    .stream()
                    .filter(p -> brandId.equals(p.getBrandId()))
                    .toList();
        }

        if (categoryId != null) {
            return productRepository.findByCategoryIdAndStatus(categoryId, "ACTIVE");
        }

        if (brandId != null) {
            return productRepository.findByBrandIdAndStatus(brandId, "ACTIVE");
        }

        return productRepository.findByStatus("ACTIVE");
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }

    @GetMapping("/products/{id}/variants")
    public List<ProductVariant> getProductVariants(@PathVariable Long id) {
        return productVariantRepository.findByProductId(id);
    }

    @GetMapping("/products/{id}/images")
    public List<ProductImage> getProductImages(@PathVariable Long id) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
    }

    @GetMapping("/products/{id}/inventory")
    public List<Inventory> getProductInventory(@PathVariable Long id) {
        return inventoryRepository.findByProductId(id);
    }

    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Product createProduct(@Valid @RequestBody ProductRequest request) {
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Category not found");
        }

        if (request.brandId() != null &&
                !brandRepository.existsById(request.brandId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Brand not found");
        }

        if (productRepository.existsBySku(request.sku().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Product SKU already exists");
        }

        String slug = makeSlug(request.name());
        validateSlug(slug, 280);

        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        validatePrices(request.basePrice(), request.salePrice());

        Product product = new Product();
        applyProductRequest(product, request, slug);
        product.setSellerId(null);

        return productRepository.save(product);
    }

    @PutMapping("/admin/products/{id}")
    @Transactional
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        if (!categoryRepository.existsById(request.categoryId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Category not found");
        }

        if (request.brandId() != null &&
                !brandRepository.existsById(request.brandId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Brand not found");
        }

        String sku = request.sku().trim();

        boolean duplicateSku = productRepository.findAll().stream()
                .anyMatch(p -> !p.getId().equals(id) &&
                        p.getSku().equalsIgnoreCase(sku));

        if (duplicateSku) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Product SKU already exists");
        }

        validatePrices(request.basePrice(), request.salePrice());

        String slug = makeSlug(request.name());
        validateSlug(slug, 280);

        final String candidateSlug = slug;

        boolean duplicateSlug = productRepository.findAll().stream()
                .anyMatch(p -> !p.getId().equals(id) &&
                        p.getSlug().equalsIgnoreCase(candidateSlug));

        if (duplicateSlug) {
            slug = candidateSlug + "-" + id;
        }

        applyProductRequest(product, request, slug);
        return productRepository.save(product);
    }

    private void validatePrices(BigDecimal basePrice, BigDecimal salePrice) {
        if (salePrice != null && salePrice.compareTo(basePrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sale price cannot be greater than base price");
        }
    }

    private void applyProductRequest(
            Product product, ProductRequest request, String slug) {

        product.setName(request.name().trim());
        product.setSlug(slug);
        product.setSku(request.sku().trim());
        product.setCategoryId(request.categoryId());
        product.setBrandId(request.brandId());
        product.setDescription(request.description());
        product.setShortDescription(request.shortDescription());
        product.setBasePrice(request.basePrice());
        product.setSalePrice(request.salePrice());
        product.setTaxRate(request.taxRate() == null
                ? BigDecimal.ZERO : request.taxRate());
        product.setStatus(normalizeStatus(request.status()));
        product.setFeatured(Boolean.TRUE.equals(request.featured()));
    }
}