package com.shopsphere.cart.service;

import java.util.UUID;

import com.shopsphere.cart.dto.checkout.CheckoutRequest;
import com.shopsphere.cart.dto.request.AddCartItemRequest;
import com.shopsphere.cart.dto.request.UpdateCartItemRequest;
import com.shopsphere.cart.dto.response.CartResponse;

public interface CartService {


    CartResponse getCart(UUID userId);

    CartResponse addItem(AddCartItemRequest request,UUID userId);

    CartResponse updateItem(UpdateCartItemRequest request,UUID userId);

    CartResponse removeItem(UUID userId,UUID productId);

    void clearCart(UUID userId);
    
    void processCheckout(CheckoutRequest request);
    
}