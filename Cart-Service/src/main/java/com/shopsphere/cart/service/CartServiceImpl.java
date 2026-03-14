package com.shopsphere.cart.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.cart.client.ProductClient;
import com.shopsphere.cart.document.CartDocument;
import com.shopsphere.cart.document.CartItem;
import com.shopsphere.cart.document.OutboxEvent;
import com.shopsphere.cart.dto.checkout.CheckoutRequest;
import com.shopsphere.cart.dto.client.ProductResponse;
import com.shopsphere.cart.dto.request.AddCartItemRequest;
import com.shopsphere.cart.dto.request.UpdateCartItemRequest;
import com.shopsphere.cart.dto.response.CartItemResponse;
import com.shopsphere.cart.dto.response.CartResponse;
import com.shopsphere.cart.enums.OutboxStatus;
import com.shopsphere.cart.event.dto.CheckoutEvent;
import com.shopsphere.cart.event.dto.CheckoutItemEvent;
import com.shopsphere.cart.repo.CartRepo;
import com.shopsphere.cart.repo.OutboxRepository;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepository;
    private final ProductClient productClient;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    public CartServiceImpl(CartRepo cartRepository, ProductClient productClient, OutboxRepository outboxRepository,
			ObjectMapper objectMapper) {
		super();
		this.cartRepository = cartRepository;
		this.productClient = productClient;
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Override
    @Cacheable(value = "cart", key = "#userId.toString()")
    public CartResponse getCart(UUID userId) {

        return cartRepository
                .findByUserId(userId)
                .map(this::mapToResponse)
                .orElseGet(() -> createEmptyCartResponse(userId));
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse addItem(AddCartItemRequest request, UUID userId) {

        validateQuantity(request.getQuantity());

        // Fetch product from product service
        ProductResponse product = productClient.getProduct(request.getProductId());

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        CartDocument cart = cartRepository
                .findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        List<CartItem> items = getOrCreateItems(cart);

        Optional<CartItem> existingItem =
                findItemByProductId(items, request.getProductId());

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());

        } else {

            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice().doubleValue());
            newItem.setQuantity(request.getQuantity());

            items.add(newItem);
        }

        recalculateCart(cart);
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse updateItem(UpdateCartItemRequest request, UUID userId) {

        validateQuantity(request.getQuantity());

        CartDocument cart = getCartOrThrow(userId);

        List<CartItem> items = getOrCreateItems(cart);

        CartItem item = findItemByProductId(items, request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        item.setQuantity(request.getQuantity());

        recalculateCart(cart);
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    @CachePut(value = "cart", key = "#userId.toString()")
    public CartResponse removeItem(UUID userId, UUID productId) {

        CartDocument cart = getCartOrThrow(userId);

        List<CartItem> items = getOrCreateItems(cart);

        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new RuntimeException("Product not present in cart");
        }

        recalculateCart(cart);
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Override
    @CacheEvict(value = "cart", key = "#userId.toString()")
    public void clearCart(UUID userId) {
        cartRepository.deleteByUserId(userId);
    }

    private CartDocument createNewCart(UUID userId) {

        CartDocument cart = new CartDocument();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.setTotalItems(0);
        cart.setTotalPrice(0.0);

        return cart;
    }

    private CartDocument getCartOrThrow(UUID userId) {

        return cartRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found for user: " + userId));
    }

    private List<CartItem> getOrCreateItems(CartDocument cart) {

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return cart.getItems();
    }

    private Optional<CartItem> findItemByProductId(List<CartItem> items, UUID productId) {

        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    private CartResponse mapToResponse(CartDocument cart) {

        CartResponse response = new CartResponse();

        response.setUserId(cart.getUserId());

        if (cart.getCartId() != null) {
            response.setCartId(cart.getCartId().toString());
        }

        List<CartItemResponse> itemResponses = new ArrayList<>();

        if (cart.getItems() != null) {

            for (CartItem item : cart.getItems()) {

                CartItemResponse itemResponse = new CartItemResponse();

                itemResponse.setProductId(item.getProductId());
                itemResponse.setProductName(item.getProductName());
                itemResponse.setPrice(item.getPrice());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setSubtotal(item.getPrice() * item.getQuantity());

                itemResponses.add(itemResponse);
            }
        }

        response.setItems(itemResponses);
        response.setTotalItems(cart.getTotalItems());
        response.setTotalPrice(cart.getTotalPrice());

        return response;
    }

    private CartResponse createEmptyCartResponse(UUID userId) {

        CartResponse response = new CartResponse();
        response.setUserId(userId);
        response.setItems(new ArrayList<>());
        response.setTotalItems(0);
        response.setTotalPrice(0.0);

        return response;
    }

    private void recalculateCart(CartDocument cart) {

        int totalItems = 0;
        double totalPrice = 0;

        if (cart.getItems() != null) {

            for (CartItem item : cart.getItems()) {

                totalItems += item.getQuantity();
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
    }

    private void validateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    @Override
    public void processCheckout(CheckoutRequest request) {

        try {

            // 1️ Fetch cart items using userId
            CartDocument cartItems = cartRepository.findByUserId(request.getUserId()).get();

            
            if (cartItems==null) {
                throw new RuntimeException("Cart is empty");
            }

            // 2️ Convert cart items to event items
            List<CartItem> list = cartItems.getItems();
            
            List<CheckoutItemEvent> items = list.stream()
                    .map(item -> new CheckoutItemEvent(
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice()
                    ))
                    .toList();

            // 3️ Create event payload
            CheckoutEvent payloadObject = new CheckoutEvent();
            payloadObject.setUserId(request.getUserId());
            payloadObject.setAddressId(request.getAddressId());
            payloadObject.setItems(items);

            String payload = objectMapper.writeValueAsString(payloadObject);

            // 4️ Create out box event
            OutboxEvent event = new OutboxEvent();
            event.setId(UUID.randomUUID().toString());
            event.setAggregateType("ORDER");
            event.setAggregateId(request.getUserId());
            event.setEventType("CHECKOUT_REQUESTED");
            event.setPayload(payload);
            event.setStatus(OutboxStatus.NEW);
            event.setCreatedAt(Instant.now());

            // 5️ Save in out box
            outboxRepository.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Checkout failed", e);
        }
    }

}