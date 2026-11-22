package com.sk.markethub.model;

public class Inventory {
    private Long id;
    private Long productId;
    private Long variantId;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;

    public Inventory() {}

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getVariantId() { return variantId; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public Integer getReservedQuantity() { return reservedQuantity; }
    public Integer getReorderLevel() { return reorderLevel; }
}
