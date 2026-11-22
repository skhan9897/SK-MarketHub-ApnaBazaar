package com.sk.markethub.model;

import java.math.BigDecimal;

public class Coupon {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minimumOrder;
    private BigDecimal maximumDiscount;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean active;

    public Coupon() {}

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getMinimumOrder() { return minimumOrder; }
    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public Integer getUsageLimit() { return usageLimit; }
    public Integer getUsedCount() { return usedCount; }
    public Boolean getActive() { return active; }
}
