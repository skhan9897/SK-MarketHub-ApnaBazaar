package com.sk.markethub.model;

public class SellerApplicationRequest {
    private String storeName;
    private String businessName;
    private String gstNumber;

    public SellerApplicationRequest(String storeName, String businessName, String gstNumber) {
        this.storeName = storeName;
        this.businessName = businessName;
        this.gstNumber = gstNumber;
    }

    public String getStoreName() { return storeName; }
    public String getBusinessName() { return businessName; }
    public String getGstNumber() { return gstNumber; }
}
