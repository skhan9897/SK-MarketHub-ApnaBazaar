package com.sk.markethub.model;

public class Review {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderItemId;
    private Integer rating;
    private String reviewTitle;
    private String reviewText;
    private Boolean verifiedPurchase;
    private String status;

    public Review() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }
    public Long getOrderItemId() { return orderItemId; }
    public Integer getRating() { return rating; }
    public String getReviewTitle() { return reviewTitle; }
    public String getReviewText() { return reviewText; }
    public Boolean getVerifiedPurchase() { return verifiedPurchase; }
    public String getStatus() { return status; }
}
