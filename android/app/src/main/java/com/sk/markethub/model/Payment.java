package com.sk.markethub.model;

import java.math.BigDecimal;

public class Payment {
    private Long id;
    private Long orderId;
    private String paymentReference;
    private String provider;
    private String paymentMethod;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private String gatewayTransactionId;

    public Payment() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getPaymentReference() { return paymentReference; }
    public String getProvider() { return provider; }
    public String getPaymentMethod() { return paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
}
