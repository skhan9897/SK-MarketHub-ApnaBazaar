package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer rating;

    @Column(name = "review_title", length = 255)
    private String reviewTitle;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "is_verified_purchase")
    private Boolean verifiedPurchase = false;

    @Column(length = 30)
    private String status = "PENDING";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

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
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public void setVerifiedPurchase(Boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    public void setStatus(String status) { this.status = status; }
}
