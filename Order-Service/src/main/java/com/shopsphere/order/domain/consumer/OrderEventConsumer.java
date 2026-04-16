package com.shopsphere.order.domain.consumer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order.domain.constants.AppConstants;
import com.shopsphere.order.domain.dto.CartItem;
import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderItemEvent;
import com.shopsphere.order.domain.dto.OrderItemList;
import com.shopsphere.order.domain.dto.OrderItemRequestDto;
import com.shopsphere.order.domain.dto.PaymentFailedEvent;
import com.shopsphere.order.domain.dto.PaymentSuccessEvent;
import com.shopsphere.order.domain.entity.OrderAddressSnapshot;
import com.shopsphere.order.domain.entity.OrderEntity;
import com.shopsphere.order.domain.entity.OutboxEntity;
import com.shopsphere.order.domain.enums.OrderStatus;
import com.shopsphere.order.domain.exception.OrderNotFoundException;
import com.shopsphere.order.domain.repo.OrderRepo;
import com.shopsphere.order.domain.repo.OutboxRepository;
import com.shopsphere.order.domain.service.OrderService;

@Component
public class OrderEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

	private final OrderService orderService;
	private final ObjectMapper objectMapper;
	private final OutboxRepository outboxRepository;
	private final OrderRepo repo;

	public OrderEventConsumer(OrderService orderService, ObjectMapper objectMapper, OutboxRepository outboxRepository,
			OrderRepo repo) {
		super();
		this.orderService = orderService;
		this.objectMapper = objectMapper;
		this.outboxRepository = outboxRepository;
		this.repo = repo;
	}

	@KafkaListener(topics = "payment-events", groupId = AppConstants.GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
	@Transactional
	public void consumePaymentEvent(String payload) {
		log.info("Received payment event payload: {}", payload);

		try {
			JsonNode rootNode = objectMapper.readTree(payload);
			String eventType = rootNode.get("eventType").asText();
			JsonNode node = rootNode.get("payload");

			switch (eventType) {
			case "PAYMENT_SUCCESS" -> handlePaymentSuccess(node);
			case "PAYMENT_FAILED" -> handlePaymentFailed(node);
			default -> {
				log.error("Received unknown payment eventType={}", eventType);
				throw new IllegalArgumentException("Unexpected payment event type: " + eventType);
			}
			}

		} catch (JsonProcessingException e) {
			log.error("Failed to parse payment event payload={}", payload, e);
		} catch (OrderNotFoundException e) {
			log.error("Order not found while processing payment event: {}", e.getMessage(), e);
		} catch (Exception e) {
			log.error("Unexpected exception while processing payment event payload={}", payload, e);
		}
	}

	private void handlePaymentSuccess(JsonNode node) throws JsonProcessingException, OrderNotFoundException {
		PaymentSuccessEvent event = objectMapper.treeToValue(node, PaymentSuccessEvent.class);

		boolean status = orderService.updateOrderStatus(event.getOrderId(), OrderStatus.PAID);

		if (status) {
			// Prepare full payload for Outbox event

			OrderEntity order = repo.findById(event.getOrderId())
					.orElseThrow(() -> new OrderNotFoundException("Order not found"));
			List<OrderItemEvent> itemEvents = order.getItems().stream()
					.map(item -> new OrderItemEvent(item.getProductSnapshot().getProductId(), item.getQunatity()))
					.toList();

			OrderAddressSnapshot addressSnapshot = order.getAddressSnapshot();

			Map<String, Object> outboxPayload = new HashMap<>();

			outboxPayload.put("orderId", order.getId());
			outboxPayload.put("userId", order.getUserId());
			outboxPayload.put("status", OrderStatus.PAID.name());
			outboxPayload.put("items", itemEvents);
			outboxPayload.put("fullName", addressSnapshot.getFullName());
			outboxPayload.put("phone", addressSnapshot.getPhone());
			outboxPayload.put("city", addressSnapshot.getCity());
			outboxPayload.put("pincode", addressSnapshot.getPincode());
			outboxPayload.put("state", addressSnapshot.getState());
			outboxPayload.put("eventType", "ORDER_PAID");
			outboxPayload.put("defaultAddress", addressSnapshot.isDefaultAddress());

			outboxPayload.put("timestamp", LocalDateTime.now());

			String payloadJson = objectMapper.writeValueAsString(outboxPayload);

			OutboxEntity entity = new OutboxEntity(UUID.randomUUID().toString(), // Unique Event ID
					"ORDER", // Aggregate type
					event.getOrderId().toString(), // Aggregate ID
					"ORDER_PAID", // Event type
					payloadJson, // Event payload
					"NEW" // Status
			);
			outboxRepository.save(entity);

			log.info("Payment success processed: orderId={} updated to PAID and Outbox event saved",
					event.getOrderId());
		} else {
			log.warn("Payment success received but order update failed for orderId={}", event.getOrderId());
		}
	}

	private void handlePaymentFailed(JsonNode node) throws JsonProcessingException, OrderNotFoundException {
		PaymentFailedEvent event = objectMapper.treeToValue(node, PaymentFailedEvent.class);

		boolean status = orderService.updateOrderStatus(event.getOrderId(), OrderStatus.FAILED);

		if (status) {
			log.warn("Payment failed processed: orderId={} updated to FAILED", event.getOrderId());
		} else {
			log.error("Payment failed received but order update failed for orderId={}", event.getOrderId());
		}
	}

	@KafkaListener(topics = "cart-events", groupId = AppConstants.GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
	@Transactional
	public void consumeCartEvent(String message) {
		log.info("Received checkout event payload: {}", message);

		try {
			OrderItemList itemList = objectMapper.readValue(message, OrderItemList.class);
			UUID userId = itemList.getUserId();

			// STEP 1: SET SECURITY CONTEXT
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("order-user", null,
					List.of(new SimpleGrantedAuthority("ROLE_USER")));

			auth.setDetails(userId);

			SecurityContextHolder.getContext().setAuthentication(auth);
			// STEP 2: BUILD REQUEST
			CreateOrderRequestDto dto = new CreateOrderRequestDto();

			dto.setAddressId(itemList.getAddressId());

			List<OrderItemRequestDto> list = new ArrayList<>();
			for (CartItem item : itemList.getItems()) {
				OrderItemRequestDto dto2 = new OrderItemRequestDto();
				dto2.setProductId(item.getProductId());
				dto2.setQuantity(item.getQuantity());
				list.add(dto2);
			}
			dto.setItems(list);

			// STEP 3: CALL SERVICE
			orderService.createOrder(userId, dto);

			log.info("Order created successfully for userId={}", userId);

		} catch (JsonProcessingException e) {
			log.error("Failed to parse checkout event payload={}", message, e);

		} catch (OrderNotFoundException e) {
			log.error("Order not found while processing checkout event: {}", e.getMessage(), e);

		} catch (Exception e) {
			log.error("Unexpected exception while processing checkout event payload={}", message, e);

		} finally {
			// STEP 4: CLEAN CONTEXT (VERY IMPORTANT)
			SecurityContextHolder.clearContext();
		}
	}
}