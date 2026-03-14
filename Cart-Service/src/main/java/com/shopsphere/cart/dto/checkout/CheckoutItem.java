package com.shopsphere.cart.dto.checkout;

public class CheckoutItem {

    private String productId;
    private int quantity;
    private double price;

    public CheckoutItem() {}

    public CheckoutItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}