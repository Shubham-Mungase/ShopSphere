package com.shopsphere.cart.event.dto;

import java.util.UUID;

public class CheckoutItemEvent {

    private UUID productId;
    private Integer quantity;
    private double price;

    public CheckoutItemEvent() {
    }

    public CheckoutItemEvent(UUID productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}