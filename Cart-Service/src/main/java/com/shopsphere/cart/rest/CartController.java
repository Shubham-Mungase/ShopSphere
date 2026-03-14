package com.shopsphere.cart.rest;


import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.cart.dto.checkout.CheckoutRequest;
import com.shopsphere.cart.dto.request.AddCartItemRequest;
import com.shopsphere.cart.dto.request.UpdateCartItemRequest;
import com.shopsphere.cart.dto.response.CartResponse;
import com.shopsphere.cart.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET CART
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable UUID userId) {

        CartResponse response = cartService.getCart(userId);

        return ResponseEntity.ok(response);
    }

    // ADD ITEM
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable UUID userId,
            @RequestBody AddCartItemRequest request) {

        CartResponse response = cartService.addItem(request, userId);

        return ResponseEntity.ok(response);
    }

    // UPDATE ITEM
    @PutMapping("/{userId}/items")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable UUID userId,
            @RequestBody UpdateCartItemRequest request) {

        CartResponse response = cartService.updateItem(request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {

        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    // CLEAR CART
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
    @PostMapping("/checkout")
    public String checkout(@RequestBody CheckoutRequest request) {

        checkoutService.processCheckout(request);

        return "Checkout event saved in outbox";
    }
}