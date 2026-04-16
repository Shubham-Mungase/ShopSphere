package com.shopsphere.order.domain.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order.domain.client.ProductServiceClient;
import com.shopsphere.order.domain.client.UserServiceClient;
import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderCreatedEvent;
import com.shopsphere.order.domain.dto.OrderItemEvent;
import com.shopsphere.order.domain.dto.OrderItemRequestDto;
import com.shopsphere.order.domain.dto.OrderItemResponseDto;
import com.shopsphere.order.domain.dto.OrderResponseDto;
import com.shopsphere.order.domain.dto.ShippingAddressResponseDto;
import com.shopsphere.order.domain.entity.OrderAddressSnapshot;
import com.shopsphere.order.domain.entity.OrderEntity;
import com.shopsphere.order.domain.entity.OrderItem;
import com.shopsphere.order.domain.entity.OrderProductSnapshot;
import com.shopsphere.order.domain.entity.OutboxEntity;
import com.shopsphere.order.domain.enums.OrderStatus;
import com.shopsphere.order.domain.exception.EventSerializationException;
import com.shopsphere.order.domain.exception.OrderNotFoundException;
import com.shopsphere.order.domain.exception.UnauthorizedAccessException;
import com.shopsphere.order.domain.repo.OrderRepo;
import com.shopsphere.order.domain.repo.OutboxRepository;
import com.shopsphere.order.domain.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepo orderRepo;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    public OrderServiceImpl(OrderRepo orderRepo, UserServiceClient userServiceClient,
                            ProductServiceClient productServiceClient, OutboxRepository outboxRepo, ObjectMapper mapper) {
        this.orderRepo = orderRepo;
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.outboxRepo = outboxRepo;
        this.mapper = mapper;
    }

    @Override
    public OrderResponseDto createOrder(UUID userId, CreateOrderRequestDto request) {
        log.info("Creating order for userId={}", userId);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.markCreated();

        log.debug("Fetching address snapshot for addressId={}", userId, request.getAddressId());
        OrderAddressSnapshot addressDto = userServiceClient.getAddressSnapshot(request.getAddressId());
        orderEntity.setAddressSnapshot(addressDto);

        BigDecimal orderTotal = BigDecimal.ZERO;
        List<OrderItemEvent> itemEvents = new ArrayList<>();

        for (OrderItemRequestDto itemRequest : request.getItems()) {
            log.debug("Fetching product snapshot for productId={}", itemRequest.getProductId());
            OrderProductSnapshot productDto = productServiceClient.getProductSnapshot(itemRequest.getProductId());

            System.err.println(productDto.getProductName());
            System.err.println(productDto.getProductId());
            
            productDto.setUserId(userId);
            BigDecimal itemTotal = productDto.getFinalPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProductSnapshot(productDto);
            orderItem.setQunatity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(productDto.getFinalPrice());
            orderItem.setTotalPrice(itemTotal);

            orderEntity.addItem(orderItem);
            orderTotal = orderTotal.add(itemTotal);

            itemEvents.add(new OrderItemEvent(productDto.getProductId(), itemRequest.getQuantity()));
        }

        orderEntity.setTotalAmount(orderTotal);
        log.info("Persisting order for userId={} totalAmount={}", userId, orderTotal);
        OrderEntity savedOrder = orderRepo.save(orderEntity);

        UUID eventId = UUID.randomUUID();
        OrderCreatedEvent createdEvent = new OrderCreatedEvent(
                eventId,
                savedOrder.getId(),
                savedOrder.getUserId(),
                "ORDER",
                itemEvents,
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().name(),
                savedOrder.getCreatedAt()
        );

        try {
            String payload = mapper.writeValueAsString(createdEvent);
            OutboxEntity entity = new OutboxEntity(
                    eventId.toString(),
                    "ORDER",
                    savedOrder.getId().toString(),
                    "OrderCreated",
                    payload,
                    "NEW"
            );
            outboxRepo.save(entity);
            log.info("Saved OrderCreatedEvent in outbox: eventId={}", eventId);
        } catch (Exception e) {
            log.error("Failed to serialize OrderCreatedEvent for orderId={}", savedOrder.getId(), e);
            throw new EventSerializationException("Failed to serialize OrderCreatedEvent", e);
        }

        log.info("Order creation completed for orderId={}", savedOrder.getId());
        return mapToDto(savedOrder);
    }

    @Override
    public OrderResponseDto getOrderById(UUID orderId, UUID userId, String role) {

        log.info("Fetching order with orderId={}", orderId);

        OrderEntity orderEntity = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId={}", orderId);
                    return new OrderNotFoundException("Order not found with id=" + orderId);
                });

        // 🔐 SECURITY CHECK (CORE FIX)
        if (!"ADMIN".equals(role) &&
            !orderEntity.getUserId().equals(userId)) {

            log.warn("Unauthorized access attempt: userId={} orderId={}", userId, orderId);
            throw new UnauthorizedAccessException("You are not allowed to access this order");
        }

        return mapToDto(orderEntity);
    }

    @Override
    public List<OrderResponseDto> getOrdersForCurrentUser(UUID userId) {
        log.info("Fetching orders for userId={}", userId);
        List<OrderEntity> list = orderRepo.findByUserId(userId);

        List<OrderResponseDto> dtos = new ArrayList<>();
        for (OrderEntity orderEntity : list) {
            dtos.add(mapToDto(orderEntity));
        }

        log.info("Fetched {} orders for userId={}", dtos.size(), userId);
        return dtos;
    }

    @Override
    public boolean updateOrderStatus(UUID orderId, OrderStatus status) {

        log.info("Updating order status: orderId={} to status={}", orderId, status);

        OrderEntity orderEntity = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for status update: orderId={}", orderId);
                    return new OrderNotFoundException("Order not found with id=" + orderId);
                });

        orderEntity.setStatus(status);
        orderRepo.save(orderEntity);

        log.info("Order status updated successfully: orderId={} status={}", orderId, status);

        return true;
    }

    
    // ---------------- MAPPING ----------------
    private OrderResponseDto mapToDto(OrderEntity order) {
        List<OrderItemResponseDto> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderProductSnapshot snapshot = item.getProductSnapshot();
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setProductId(snapshot.getProductId());
            itemDto.setProductName(snapshot.getProductName());
            itemDto.setImageUrl(snapshot.getImageUrl());
            itemDto.setQuantity(item.getQunatity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            itemDto.setTotalPrice(item.getTotalPrice());
            itemResponses.add(itemDto);
        }

        OrderAddressSnapshot address = order.getAddressSnapshot();
        ShippingAddressResponseDto addressDto = new ShippingAddressResponseDto();
        addressDto.setName(address.getFullName());
        addressDto.setPhone(address.getPhone());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setPincode(address.getPincode());

        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(order.getId());
        response.setOrderStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(itemResponses);
        response.setShippingAddress(addressDto);

        return response;
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {

        log.info("Fetching all orders (ADMIN)");

        List<OrderEntity> all = orderRepo.findAll();

        List<OrderResponseDto> dtos = new ArrayList<>();
        for (OrderEntity order : all) {
            dtos.add(mapToDto(order));
        }

        return dtos;
    }
}