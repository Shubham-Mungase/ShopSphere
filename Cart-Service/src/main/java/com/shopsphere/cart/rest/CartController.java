package com.shopsphere.cart.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.cart.dto.checkout.CheckoutRequest;
import com.shopsphere.cart.dto.request.AddCartItemRequest;
import com.shopsphere.cart.dto.request.UpdateCartItemRequest;
import com.shopsphere.cart.dto.response.ApiResponse;
import com.shopsphere.cart.dto.response.CartResponse;
import com.shopsphere.cart.filter.UserContext;
import com.shopsphere.cart.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 🔥 Get UserContext
    private UserContext getUser() {
        return (UserContext) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
    }

    // ✅ GET CART
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {

        UserContext user = getUser();
        CartResponse cart = cartService.getCart(user.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart fetched successfully", cart)
        );
    }

    // ✅ ADD ITEM
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @RequestBody AddCartItemRequest request) {

        UserContext user = getUser();
        CartResponse cart = cartService.addItem(request, user.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Item added to cart", cart)
        );
    }

    // ✅ UPDATE ITEM
    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @RequestBody UpdateCartItemRequest request) {

        UserContext user = getUser();
        CartResponse cart = cartService.updateItem(request, user.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart item updated", cart)
        );
    }

    // ✅ REMOVE ITEM
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable UUID productId) {

        UserContext user = getUser();
        CartResponse cart = cartService.removeItem(user.getUserId(), productId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Item removed from cart", cart)
        );
    }

    // ✅ CLEAR CART
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {

        UserContext user = getUser();
        cartService.clearCart(user.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart cleared successfully", null)
        );
    }

    // ✅ CHECKOUT
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<String>> checkout(
            @RequestBody CheckoutRequest request) {

        UserContext user = getUser();
        request.setUserId(user.getUserId());

        cartService.processCheckout(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Checkout event published", "SUCCESS")
        );
    }
}