package com.sk.markethub.model;

import java.math.BigDecimal;

public class Refund {
    private Long id;
    private Long orderId;
    private Long paymentId;
    private BigDecimal refundAmount;
    private String reason;
    private String refundStatus;

    public Refund() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getPaymentId() { return paymentId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public String getReason() { return reason; }
    public String getRefundStatus() { return refundStatus; }
}
