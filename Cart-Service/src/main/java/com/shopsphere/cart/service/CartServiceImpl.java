package com.shopsphere.cart.service;

import java.time.Instant;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.cart.client.ProductClient;
import com.shopsphere.cart.document.*;
import com.shopsphere.cart.dto.checkout.CheckoutRequest;
import com.shopsphere.cart.dto.client.ApiResponse;
import com.shopsphere.cart.dto.client.ProductResponse;
import com.shopsphere.cart.dto.request.*;
import com.shopsphere.cart.dto.response.*;
import com.shopsphere.cart.enums.OutboxStatus;
import com.shopsphere.cart.event.dto.*;
import com.shopsphere.cart.exceptions.*;
import com.shopsphere.cart.repo.*;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepo cartRepository;
    private final ProductClient productClient;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public CartServiceImpl(CartRepo cartRepository,
                           ProductClient productClient,
                           OutboxRepository outboxRepository,
                           ObjectMapper objectMapper) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Cacheable(value = "cart", key = "#userId.toString()")
    public CartResponse getCart(UUID userId) {

        log.info("Fetching cart for userId: {}", userId);

        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    log.info("Cart found for userId: {}", userId);
                    return mapToResponse(cart);
                })
                .orElseGet(() -> {
                    log.warn("Cart not found, returning empty cart");
                    return createEmptyCartResponse(userId);
                });
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse addItem(AddCartItemRequest request, UUID userId) {

        log.info("Add item request: userId={}, productId={}, quantity={}",
                userId, request.getProductId(), request.getQuantity());

        validateQuantity(request.getQuantity());

         ApiResponse<ProductResponse> apiResponse = productClient.getProduct(request.getProductId());
         ProductResponse product = apiResponse.getData();
//        System.err.println(product.getPrice());
//
//        System.err.println(product.getName());
//
//        System.err.println(product.getId());
//
//        System.err.println(product.getCategory());
//
//        System.err.println(product.getName());
        if (product == null) {
            log.error("Product not found: {}", request.getProductId());
            throw new ProductNotFoundException("Product not found");
        }

        CartDocument cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        List<CartItem> items = getOrCreateItems(cart);

        Optional<CartItem> existingItem = findItemByProductId(items, request.getProductId());

        if (existingItem.isPresent()) {
            log.info("Updating existing item");
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            log.info("Adding new item");
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice().doubleValue());
            newItem.setQuantity(request.getQuantity());
            items.add(newItem);
        }

        recalculateCart(cart);
        cartRepository.save(cart);

        log.info("Item added successfully");

        return mapToResponse(cart);
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse updateItem(UpdateCartItemRequest request, UUID userId) {

        log.info("Update item request: {}", request.getProductId());

        validateQuantity(request.getQuantity());

        CartDocument cart = getCartOrThrow(userId);

        CartItem item = findItemByProductId(cart.getItems(), request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not in cart"));

        item.setQuantity(request.getQuantity());

        recalculateCart(cart);
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse removeItem(UUID userId, UUID productId) {

        log.info("Removing item: {}", productId);

        CartDocument cart = getCartOrThrow(userId);

        boolean removed = cart.getItems().removeIf(i -> i.getProductId().equals(productId));

        if (!removed) {
            throw new ProductNotFoundException("Product not in cart");
        }

        recalculateCart(cart);
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    @CacheEvict(value = "cart", key = "#userId.toString()")
    public void clearCart(UUID userId) {
        log.info("Clearing cart for userId: {}", userId);
        cartRepository.deleteByUserId(userId);
    }

    @Override
    public void processCheckout(CheckoutRequest request) {

        log.info("Checkout started for userId: {}", request.getUserId());

        try {

            CartDocument cart = cartRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new CartNotFoundException("Cart is empty"));

            List<CheckoutItemEvent> items = cart.getItems().stream()
                    .map(i -> new CheckoutItemEvent(
                            i.getProductId(),
                            i.getQuantity(),
                            i.getPrice()))
                    .toList();

            CheckoutEvent payloadObj = new CheckoutEvent();
            payloadObj.setUserId(request.getUserId());
            payloadObj.setAddressId(request.getAddressId());
            payloadObj.setItems(items);

            String payload = objectMapper.writeValueAsString(payloadObj);

            OutboxEvent event = new OutboxEvent();
            event.setId(UUID.randomUUID().toString());
            event.setAggregateType("ORDER");
            event.setAggregateId(request.getUserId());
            event.setEventType("CHECKOUT_REQUESTED");
            event.setPayload(payload);
            event.setStatus(OutboxStatus.NEW);
            event.setCreatedAt(Instant.now());

            outboxRepository.save(event);

            log.info("Checkout event saved");

        } catch (Exception e) {
            log.error("Checkout failed", e);
            throw new CheckoutException("Checkout failed", e);
        }
    }

    // ===== PRIVATE METHODS =====

    private CartDocument createNewCart(UUID userId) {
        CartDocument cart = new CartDocument();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.setTotalItems(0);
        cart.setTotalPrice(0.0);
        return cart;
    }

    private CartDocument getCartOrThrow(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));
    }

    private List<CartItem> getOrCreateItems(CartDocument cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        return cart.getItems();
    }

    private Optional<CartItem> findItemByProductId(List<CartItem> items, UUID productId) {
        return items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
    }

    private void recalculateCart(CartDocument cart) {
        int totalItems = 0;
        double totalPrice = 0;

        for (CartItem item : cart.getItems()) {
            totalItems += item.getQuantity();
            totalPrice += item.getPrice() * item.getQuantity();
        }

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    private CartResponse mapToResponse(CartDocument cart) {

        CartResponse res = new CartResponse();
        res.setUserId(cart.getUserId());

        List<CartItemResponse> items = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            CartItemResponse r = new CartItemResponse();
            r.setProductId(item.getProductId());
            r.setProductName(item.getProductName());
            r.setPrice(item.getPrice());
            r.setQuantity(item.getQuantity());
            r.setSubtotal(item.getPrice() * item.getQuantity());
            items.add(r);
        }

        res.setItems(items);
        res.setTotalItems(cart.getTotalItems());
        res.setTotalPrice(cart.getTotalPrice());

        return res;
    }

    private CartResponse createEmptyCartResponse(UUID userId) {
        CartResponse res = new CartResponse();
        res.setUserId(userId);
        res.setItems(new ArrayList<>());
        res.setTotalItems(0);
        res.setTotalPrice(0.0);
        return res;
    }
}