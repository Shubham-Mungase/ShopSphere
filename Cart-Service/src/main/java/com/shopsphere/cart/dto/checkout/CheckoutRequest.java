package com.shopsphere.cart.dto.checkout;

import java.util.UUID;

public class CheckoutRequest {

    private UUID userId;
    private UUID addressId;

    public CheckoutRequest() {}

    public CheckoutRequest(UUID userId, UUID addressId ) {
        this.userId = userId;
        this.addressId = addressId;
    }

    public UUID getUserId() { return userId; }
    public UUID getAddressId() { return addressId; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setAddressId(UUID addressId) { this.addressId = addressId; }
}