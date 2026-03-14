package com.shopsphere.cart.dto.checkout;

import java.util.List;
import java.util.UUID;

public class CheckoutRequest {

    private UUID userId;
    private UUID addressId;
    private List<CheckoutItem> items;

    public CheckoutRequest() {}

    public CheckoutRequest(UUID userId, UUID addressId, List<CheckoutItem> items) {
        this.userId = userId;
        this.addressId = addressId;
        this.items = items;
    }

    public UUID getUserId() { return userId; }
    public UUID getAddressId() { return addressId; }
    public List<CheckoutItem> getItems() { return items; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setAddressId(UUID addressId) { this.addressId = addressId; }
    public void setItems(List<CheckoutItem> items) { this.items = items; }
}