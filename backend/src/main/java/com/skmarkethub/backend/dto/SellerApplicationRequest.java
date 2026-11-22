package com.skmarkethub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerApplicationRequest(

    @NotBlank(message = "Store name is required")
    @Size(max = 180, message = "Store name cannot exceed 180 characters")
    String storeName,

    @Size(max = 180, message = "Business name cannot exceed 180 characters")
    String businessName,

    @Size(max = 30, message = "GST number cannot exceed 30 characters")
    String gstNumber
) {}