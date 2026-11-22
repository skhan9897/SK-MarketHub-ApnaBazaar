package com.skmarkethub.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "refund_status", length = 40)
    private String refundStatus = "REQUESTED";

    @Column(name = "gateway_refund_id", length = 180)
    private String gatewayRefundId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Refund() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getPaymentId() { return paymentId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public String getReason() { return reason; }
    public String getRefundStatus() { return refundStatus; }
    public String getGatewayRefundId() { return gatewayRefundId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public void setReason(String reason) { this.reason = reason; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }
    public void setGatewayRefundId(String gatewayRefundId) { this.gatewayRefundId = gatewayRefundId; }
}
