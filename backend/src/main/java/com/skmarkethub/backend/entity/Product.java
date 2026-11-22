package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(length = 30)
    private String status = "DRAFT";

    @Column(name = "is_featured")
    private Boolean featured = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Product() {}
    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public Long getCategoryId() { return categoryId; }
    public Long getBrandId() { return brandId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public String getShortDescription() { return shortDescription; }
    public BigDecimal getBasePrice() { return basePrice; }
    public BigDecimal getSalePrice() { return salePrice; }
    public BigDecimal getTaxRate() { return taxRate; }
    public String getStatus() { return status; }
    public Boolean getFeatured() { return featured; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setSellerId(Long v) { sellerId = v; }
    public void setCategoryId(Long v) { categoryId = v; }
    public void setBrandId(Long v) { brandId = v; }
    public void setName(String v) { name = v; }
    public void setSlug(String v) { slug = v; }
    public void setSku(String v) { sku = v; }
    public void setDescription(String v) { description = v; }
    public void setShortDescription(String v) { shortDescription = v; }
    public void setBasePrice(BigDecimal v) { basePrice = v; }
    public void setSalePrice(BigDecimal v) { salePrice = v; }
    public void setTaxRate(BigDecimal v) { taxRate = v; }
    public void setStatus(String v) { status = v; }
    public void setFeatured(Boolean v) { featured = v; }
}