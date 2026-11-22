package com.sk.markethub.model;

import java.math.BigDecimal;
import java.util.List;

public class CartSummary {
    private Long cartId;
    private List<CartItemDetail> items;
    private BigDecimal subtotal;
    private Integer itemCount;

    public CartSummary() {}

    public Long getCartId() { return cartId; }
    public List<CartItemDetail> getItems() { return items; }
    public BigDecimal getSubtotal() { return subtotal; }
    public Integer getItemCount() { return itemCount; }
}
