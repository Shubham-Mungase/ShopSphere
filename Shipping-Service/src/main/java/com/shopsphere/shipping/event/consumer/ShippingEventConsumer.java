package com.shopsphere.shipping.event.consumer;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.shipping.dto.event.OrderPaidEvent;
import com.shopsphere.shipping.entity.ShipmentAddress;
import com.shopsphere.shipping.service.ShipmentService;

@Component
public class ShippingEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(ShippingEventConsumer.class);

	private final ShipmentService shipmentService;
	private final ObjectMapper objectMapper;

	public ShippingEventConsumer(ShipmentService shipmentService, ObjectMapper objectMapper) {
		this.shipmentService = shipmentService;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "order-events", groupId = "shipping-group", containerFactory = "kafkaListenerContainerFactory")
	public void consumeOrderPaidEvent(String event, Acknowledgment ack) {
		
		
		
		try {
			// Deserialize
			OrderPaidEvent value = objectMapper.readValue(event, OrderPaidEvent.class);
			if (!"ORDER_PAID".equals(value.getEventType())) {
				log.info("Received event: {} but this is not related to SHIPPING SERVICE", event);
			    ack.acknowledge();
			    return;
			}
			if (!"PAID".equalsIgnoreCase(value.getStatus())) {
			    ack.acknowledge();
			    return;
			}
			log.info("Received event: {}", event);

			UUID userId = value.getUserId();

			// security

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("shipping-user", null,
					List.of(new SimpleGrantedAuthority("ROLE_USER")));

			auth.setDetails(userId);

			SecurityContextHolder.getContext().setAuthentication(auth);

			// Idempotency check
			if (shipmentService.isShipmentAlreadyCreated(value.getOrderId())) {
				log.warn(" Duplicate event ignored for orderId={}", value.getOrderId());
				ack.acknowledge();
				return;
			}
			if (value.getOrderId() == null || value.getItems() == null || value.getItems().isEmpty()) {
				throw new RuntimeException("Invalid event data");
			}

			ShipmentAddress shipmentAddress = new ShipmentAddress();
			shipmentAddress.setFullName(value.getFullName());
			shipmentAddress.setPhone(value.getPhone());
			shipmentAddress.setCity(value.getCity());
			shipmentAddress.setPincode(value.getPincode());
			shipmentAddress.setState(value.getState());
			shipmentAddress.setIsDefault(value.isDefaultAddress());

			// Business logic (UPDATED)
			shipmentService.createShipment(value.getOrderId(), value.getItems(), shipmentAddress);

			log.info(" Shipment(s) created successfully for orderId={}", value.getOrderId());

			// Manual commit
			ack.acknowledge();

		} catch (Exception ex) {

			log.error(" Error processing event: {}", event, ex);

		} finally {
			SecurityContextHolder.clearContext();
		}
	}
}