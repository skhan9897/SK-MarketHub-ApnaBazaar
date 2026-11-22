package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "variant_name", length = 150)
    private String variantName;

    @Column(columnDefinition = "JSON")
    private String attributes;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    public ProductVariant() {}
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getVariantName() { return variantName; }
    public String getAttributes() { return attributes; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setProductId(Long v) { productId = v; }
    public void setSku(String v) { sku = v; }
    public void setVariantName(String v) { variantName = v; }
    public void setAttributes(String v) { attributes = v; }
    public void setPrice(BigDecimal v) { price = v; }
    public void setSalePrice(BigDecimal v) { salePrice = v; }
}