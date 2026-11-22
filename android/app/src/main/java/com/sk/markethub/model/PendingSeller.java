package com.sk.markethub.model;

public class PendingSeller {
    private Long sellerId;
    private Long userId;
    private String email;
    private String mobile;
    private String firstName;
    private String lastName;
    private String storeName;
    private String businessName;
    private String gstNumber;
    private String verificationStatus;
    private String status;

    public PendingSeller() {}

    public Long getSellerId() { return sellerId; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStoreName() { return storeName; }
    public String getBusinessName() { return businessName; }
    public String getGstNumber() { return gstNumber; }
    public String getVerificationStatus() { return verificationStatus; }
    public String getStatus() { return status; }
}
