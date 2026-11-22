package com.sk.markethub.model;

public class Shipment {
    private Long id;
    private Long orderId;
    private String courierName;
    private String trackingNumber;
    private String shipmentStatus;
    private String shippedAt;
    private String deliveredAt;

    public Shipment() {}

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getCourierName() { return courierName; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getShipmentStatus() { return shipmentStatus; }
    public String getShippedAt() { return shippedAt; }
    public String getDeliveredAt() { return deliveredAt; }
}
