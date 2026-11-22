package com.sk.markethub.model;

import java.math.BigDecimal;

public class CartItemDetail {
    private Long itemId;
    private Long productId;
    private String productName;
    private String sku;
    private Long variantId;
    private String variantName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;

    public CartItemDetail() {}

    public Long getItemId() { return itemId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public Long getVariantId() { return variantId; }
    public String getVariantName() { return variantName; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}
