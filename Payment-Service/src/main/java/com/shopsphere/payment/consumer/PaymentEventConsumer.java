package com.shopsphere.payment.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.payment.common.event.DomainEvent;
import com.shopsphere.payment.common.event.InventoryReservedEvent;
import com.shopsphere.payment.constants.AppConstants;
import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.service.PaymentService;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final PaymentService paymentService;
    private final ObjectMapper mapper;

    public PaymentEventConsumer(PaymentService paymentService, ObjectMapper mapper) {
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @KafkaListener(
        topics = "inventory-events",
        groupId = AppConstants.GROUP_ID,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerInventoryEvent(String message) throws Exception {

        log.info("Received message from inventory-events: {}", message);

        try {
            DomainEvent<?> domainEvent = mapper.readValue(message, DomainEvent.class);

            if ("INVENTORY_RESERVED".equals(domainEvent.getEventType())) {

                InventoryReservedEvent reservedEvent =
                        mapper.convertValue(domainEvent.getPayload(), InventoryReservedEvent.class);

                PaymentRequestDto dto = new PaymentRequestDto();

                log.info("Order Event Received: orderId={}", reservedEvent.getOrderId());

                dto.setOrderId(reservedEvent.getOrderId());
                dto.setUserId(reservedEvent.getUserId());
                dto.setAmount(reservedEvent.getTotalAmount());

                paymentService.createPendingPayment(dto);

                log.info("Payment initiated for orderId={}", reservedEvent.getOrderId());
            } else {
                log.debug("Ignoring eventType={}", domainEvent.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing inventory event message={}", message, e);
            throw e; // rethrow so Kafka can retry / DLQ (important for reliability)
        }
    }
}