package com.skmarkethub.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_primary")
    private Boolean primary = false;

    public ProductImage() {}
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getImageUrl() { return imageUrl; }
    public String getAltText() { return altText; }
    public Integer getDisplayOrder() { return displayOrder; }
    public Boolean getPrimary() { return primary; }
    public void setProductId(Long v) { productId = v; }
    public void setImageUrl(String v) { imageUrl = v; }
    public void setAltText(String v) { altText = v; }
    public void setDisplayOrder(Integer v) { displayOrder = v; }
    public void setPrimary(Boolean v) { primary = v; }
}