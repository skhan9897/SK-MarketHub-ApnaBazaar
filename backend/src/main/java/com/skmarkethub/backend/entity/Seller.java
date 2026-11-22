package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "store_name", nullable = false, length = 180)
    private String storeName;

    @Column(name = "business_name", length = 180)
    private String businessName;

    @Column(name = "gst_number", length = 30)
    private String gstNumber;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate = BigDecimal.ZERO;

    @Column(name = "verification_status", length = 30)
    private String verificationStatus = "PENDING";

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Seller() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public String getStoreName() { return storeName; }
    public String getBusinessName() { return businessName; }
    public String getGstNumber() { return gstNumber; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public void setStatus(String status) { this.status = status; }
}