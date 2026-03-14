package com.shopsphere.cart.dto.response;

import java.util.List;
import java.util.UUID;

public class CartResponse {

    private String cartId;
    private UUID userId;
    private List<CartItemResponse> items;
    private Double totalPrice;
    private Integer totalItems;

    public CartResponse() {
    }

    public CartResponse(String cartId, UUID userId, List<CartItemResponse> items, Double totalPrice, Integer totalItems) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.totalItems = totalItems;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}