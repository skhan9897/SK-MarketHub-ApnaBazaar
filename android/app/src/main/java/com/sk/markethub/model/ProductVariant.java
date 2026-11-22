package com.sk.markethub.model;

import java.math.BigDecimal;

public class ProductVariant {
    private Long id;
    private Long productId;
    private String sku;
    private String variantName;
    private String attributes;
    private BigDecimal price;
    private BigDecimal salePrice;

    public ProductVariant() {}

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public String getAttributes() { return attributes; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSalePrice() { return salePrice; }
}
