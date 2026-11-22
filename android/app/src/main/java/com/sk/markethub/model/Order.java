package com.sk.markethub.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private Long orderId;
    private String orderNumber;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String createdAt;
    private List<OrderItem> items;
    private Payment payment;
    private Shipment shipment;

    public Order() {}

    public Long getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getShippingAmount() { return shippingAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
    public Payment getPayment() { return payment; }
    public Shipment getShipment() { return shipment; }
}
