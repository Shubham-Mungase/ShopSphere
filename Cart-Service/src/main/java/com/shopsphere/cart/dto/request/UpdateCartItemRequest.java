package com.shopsphere.cart.dto.request;

import java.util.UUID;

public class UpdateCartItemRequest {

    private UUID productId;
    private Integer quantity;

    public UpdateCartItemRequest() {
    }

    public UpdateCartItemRequest(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}