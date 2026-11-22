package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 5;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Inventory() {}
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getVariantId() { return variantId; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public Integer getReservedQuantity() { return reservedQuantity; }
    public Integer getReorderLevel() { return reorderLevel; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setProductId(Long v) { productId = v; }
    public void setVariantId(Long v) { variantId = v; }
    public void setAvailableQuantity(Integer v) { availableQuantity = v; }
    public void setReservedQuantity(Integer v) { reservedQuantity = v; }
    public void setReorderLevel(Integer v) { reorderLevel = v; }
}