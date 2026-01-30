package com.shopsphere.order.domain.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.order.domain.client.ProductServiceClient;
import com.shopsphere.order.domain.client.UserServiceClient;
import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderItemRequestDto;
import com.shopsphere.order.domain.dto.OrderItemResponseDto;
import com.shopsphere.order.domain.dto.OrderResponseDto;
import com.shopsphere.order.domain.dto.ShippingAddressResponseDto;
import com.shopsphere.order.domain.entity.OrderAddressSnapshot;
import com.shopsphere.order.domain.entity.OrderEntity;
import com.shopsphere.order.domain.entity.OrderItem;
import com.shopsphere.order.domain.entity.OrderProductSnapshot;
import com.shopsphere.order.domain.enums.OrderStatus;
import com.shopsphere.order.domain.messages.OrderEventMapper;
import com.shopsphere.order.domain.messages.OrderEventPublisher;
import com.shopsphere.order.domain.repo.OrderRepo;
import com.shopsphere.order.domain.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepo orderRepo;
	private final UserServiceClient userServiceClient;
	private final ProductServiceClient productServiceClient;
	
	
	private final OrderEventPublisher eventPublisher;

	

	public OrderServiceImpl(OrderRepo orderRepo, UserServiceClient userServiceClient,
			ProductServiceClient productServiceClient, OrderEventPublisher eventPublisher) {
		super();
		this.orderRepo = orderRepo;
		this.userServiceClient = userServiceClient;
		this.productServiceClient = productServiceClient;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public OrderResponseDto createOrder(UUID userId, CreateOrderRequestDto request) {

		// 1️⃣ Create Order Aggregate Root
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setUserId(userId);
		orderEntity.markCreated();

		// 2️⃣ Fetch Address Snapshot from User Service (DTO)
		OrderAddressSnapshot addressDto = userServiceClient.getAddressSnapshot(userId, request.getAddressId());

		// 3️⃣ Map Address Snapshot (Order side)
		OrderAddressSnapshot orderAddressSnapshot = new OrderAddressSnapshot();
		orderAddressSnapshot.setFullName(addressDto.getFullName());
		orderAddressSnapshot.setPhone(addressDto.getPhone());
		orderAddressSnapshot.setCity(addressDto.getCity());
		orderAddressSnapshot.setState(addressDto.getState());
		orderAddressSnapshot.setPincode(addressDto.getPincode());

		orderEntity.setAddressSnapshot(orderAddressSnapshot);

		// 4️⃣ Process Order Items + Product Snapshots
		BigDecimal orderTotal = BigDecimal.ZERO;

		for (OrderItemRequestDto itemRequest : request.getItems()) {

			// Fetch Product Snapshot DTO
			OrderProductSnapshot productDto = productServiceClient.getProductSnapshot(itemRequest.getProductId());

			// Map Product Snapshot (Order side)
			OrderProductSnapshot productSnapshot = new OrderProductSnapshot();
			productSnapshot.setUserId(userId);
			productSnapshot.setProductId(productDto.getProductId());
			productSnapshot.setProductName(productDto.getProductName());
			productSnapshot.setImageUrl(productDto.getImageUrl());
			productSnapshot.setPrice(productDto.getPrice());
			productSnapshot.setDiscount(productDto.getDiscount());
			productSnapshot.setFinalPrice(productDto.getFinalPrice());

			// Calculate item total
			BigDecimal itemTotal = productDto.getFinalPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

			// Create Order Item
			OrderItem orderItem = new OrderItem();
			orderItem.setProductSnapshot(productSnapshot);
			orderItem.setQunatity(itemRequest.getQuantity());
			orderItem.setPriceAtPurchase(productDto.getFinalPrice());
			orderItem.setTotalPrice(itemTotal);

			// Attach to aggregate
			orderEntity.addItem(orderItem);

			// Accumulate order total
			orderTotal = orderTotal.add(itemTotal);
		}

		// 5️⃣ Set Final Order Total
		orderEntity.setTotalAmount(orderTotal);

		// 6️⃣ Persist Aggregate (CASCADE saves everything)
		OrderEntity savedOrder = orderRepo.save(orderEntity);

		//event publishing using kafka
		
		eventPublisher.publishEvent(OrderEventMapper.createdEvent(orderEntity)
				);
		
		
		// 7️⃣ Map Response DTO
		return mapToDto(savedOrder);
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
		// response.setCreatedAt(order.getCreatedAt());

		return response;
	}

	@Override
	public OrderResponseDto getOrderById(UUID orderId) {

		OrderEntity orderEntity = orderRepo.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with this id=" + orderId));

		List<OrderItemResponseDto> itemDtos = new ArrayList<>();

		for (OrderItem item : orderEntity.getItems()) {

			OrderProductSnapshot ps = item.getProductSnapshot();

			OrderItemResponseDto itemDto = new OrderItemResponseDto();

			itemDto.setImageUrl(ps.getImageUrl());
			itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
			itemDto.setProductId(ps.getProductId());
			itemDto.setQuantity(item.getQunatity());
			itemDto.setProductName(ps.getProductName());
			itemDto.setTotalPrice(item.getTotalPrice());

			itemDtos.add(itemDto);
		}

		ShippingAddressResponseDto dto2 = new ShippingAddressResponseDto();
		OrderAddressSnapshot as = orderEntity.getAddressSnapshot();
		dto2.setCity(as.getCity());
		dto2.setName(as.getFullName());
		dto2.setPhone(as.getPhone());
		dto2.setPincode(as.getPincode());
		dto2.setState(as.getState());

		OrderResponseDto dto = new OrderResponseDto();
		dto.setCreatedAt(orderEntity.getCreatedAt());
		dto.setItems(itemDtos);
		dto.setOrderId(orderEntity.getId());
		dto.setOrderStatus(orderEntity.getStatus().name());
		dto.setTotalAmount(orderEntity.getTotalAmount());

		dto.setShippingAddress(dto2);

		return dto;

	}

	@Override
	public List<OrderResponseDto> getOrdersForCurrentUser(UUID userId) {
		List<OrderEntity> list = orderRepo.findByUserId(userId);
		
		List<OrderResponseDto> dtos=new ArrayList<>();
		for (OrderEntity orderEntity : list) {
			List<OrderItemResponseDto> itemDtos = new ArrayList<>();

			for (OrderItem item : orderEntity.getItems()) {
				OrderProductSnapshot ps = item.getProductSnapshot();
				OrderItemResponseDto itemDto = new OrderItemResponseDto();
				itemDto.setImageUrl(ps.getImageUrl());
				itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
				itemDto.setProductId(ps.getProductId());
				itemDto.setQuantity(item.getQunatity());
				itemDto.setProductName(ps.getProductName());
				itemDto.setTotalPrice(item.getTotalPrice());

				itemDtos.add(itemDto);
			}

			ShippingAddressResponseDto dto2 = new ShippingAddressResponseDto();
			OrderAddressSnapshot as = orderEntity.getAddressSnapshot();
			dto2.setCity(as.getCity());
			dto2.setName(as.getFullName());
			dto2.setPhone(as.getPhone());
			dto2.setPincode(as.getPincode());
			dto2.setState(as.getState());

			OrderResponseDto dto = new OrderResponseDto();
			dto.setCreatedAt(orderEntity.getCreatedAt());
			dto.setItems(itemDtos);
			dto.setOrderId(orderEntity.getId());
			dto.setOrderStatus(orderEntity.getStatus().name());
			dto.setTotalAmount(orderEntity.getTotalAmount());

			dto.setShippingAddress(dto2);
			
			dtos.add(dto);
		
		}
		return dtos;
		
	}

	@Override
	public void updateOrderStatus(UUID orderId, OrderStatus status) {
		
		Optional<OrderEntity> order = orderRepo.findById(orderId);
		if(order!=null)
		{
			OrderEntity orderEntity = order.get();
			orderEntity.setStatus(status);
			OrderEntity orderEntity2 = orderRepo.save(orderEntity);
			// eventPublisher.publishEvent(OrderEventMapper.createdEvent(orderEntity2)
				//	);
		}
	
	}
}
