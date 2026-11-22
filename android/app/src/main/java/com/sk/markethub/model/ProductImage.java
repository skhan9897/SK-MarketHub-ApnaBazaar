package com.sk.markethub.model;

public class ProductImage {
    private Long id;
    private Long productId;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
    private Boolean primary;

    public ProductImage() {}

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getImageUrl() { return imageUrl; }
    public String getAltText() { return altText; }
    public Integer getDisplayOrder() { return displayOrder; }
    public Boolean getPrimary() { return primary; }
}
