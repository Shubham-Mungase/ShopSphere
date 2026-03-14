package com.shopsphere.cart.event.dto;


import java.util.List;
import java.util.UUID;

public class CheckoutEvent {

    private UUID userId;
    private UUID addressId;
    private List<CheckoutItemEvent> items;

    public CheckoutEvent() {
    }

    public CheckoutEvent(UUID userId, UUID addressId, List<CheckoutItemEvent> items) {
        this.userId = userId;
        this.addressId = addressId;
        this.items = items;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public List<CheckoutItemEvent> getItems() {
        return items;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public void setItems(List<CheckoutItemEvent> items) {
        this.items = items;
    }
}