package com.sk.markethub.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal taxRate;
    private String status;
    private Boolean featured;

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
}
