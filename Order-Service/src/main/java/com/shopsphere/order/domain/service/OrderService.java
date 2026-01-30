package com.shopsphere.order.domain.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderResponseDto;
import com.shopsphere.order.domain.enums.OrderStatus;

public interface OrderService {

	OrderResponseDto createOrder(UUID userId, CreateOrderRequestDto request);

	OrderResponseDto getOrderById(UUID orderId);

	List<OrderResponseDto> getOrdersForCurrentUser(UUID uuid);

	void updateOrderStatus(UUID orderId, OrderStatus status);

}
