package com.shopsphere.payment.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.payment.common.event.DomainEvent;
import com.shopsphere.payment.common.event.PaymentFailedEvent;
import com.shopsphere.payment.common.event.PaymentSuccessEvent;
import com.shopsphere.payment.entity.OutboxEntity;
import com.shopsphere.payment.entity.PayementEntity;
import com.shopsphere.payment.enums.OutboxStatus;
import com.shopsphere.payment.enums.PaymentStatus;
import com.shopsphere.payment.exception.PaymentEventCreationException;
import com.shopsphere.payment.exception.WebhookPayloadException;
import com.shopsphere.payment.exception.WebhookSignatureException;
import com.shopsphere.payment.repo.OutboxRepo;
import com.shopsphere.payment.repo.PaymentRepo;
import com.shopsphere.payment.utils.WebhookSignatureVerifier;

@Service
public class WebHookService {

    private static final Logger log = LoggerFactory.getLogger(WebHookService.class);

    private final WebhookSignatureVerifier verifier;
    private final ObjectMapper mapper;
    private final PaymentRepo repo;
    private final OutboxRepo outboxRepo;

    @Value("${payment.webhook.secret}")
    private String webHookSecret;

    public WebHookService(WebhookSignatureVerifier verifier,
                          ObjectMapper mapper,
                          PaymentRepo repo,
                          OutboxRepo outboxRepo) {
        this.verifier = verifier;
        this.mapper = mapper;
        this.repo = repo;
        this.outboxRepo = outboxRepo;
    }

    @Transactional
    public void processWebHook(String rawPayload, String signature) {

        // 1️⃣ Verify signature
        if (!verifier.verify(rawPayload, signature, webHookSecret)) {
            log.warn("Invalid webhook signature: payload={}, signature={}", rawPayload, signature);
            throw new WebhookSignatureException("Invalid webhook signature");
        }
        log.info("Webhook signature verified successfully");

        JsonNode root;
        try {
            root = mapper.readTree(rawPayload);
        } catch (Exception e) {
            log.error("Failed to parse webhook payload: {}", rawPayload, e);
            throw new WebhookPayloadException("Invalid webhook payload", e);
        }

        String eventType = root.path("event").asText();

        if (!eventType.startsWith("payment.")) {
            log.info("Ignoring non-payment webhook event: {}", eventType);
            return;
        }

        JsonNode paymentNode = root.path("payload").path("payment").path("entity");
        if (paymentNode.isMissingNode()) {
            log.error("Payment node missing in webhook payload: {}", rawPayload);
            throw new WebhookPayloadException("Payment node missing", null);
        }

        String gatewayPaymentId = paymentNode.path("id").asText();
        String gatewayOrderId = paymentNode.path("order_id").asText();

        PayementEntity entity = repo.findByGatewayOrderId(gatewayOrderId).orElse(null);

        if (entity == null) {
            log.warn("Payment not found for gatewayOrderId={}", gatewayOrderId);
            return;
        }

        PaymentStatus newStatus = mapStatusByEvent(eventType);

        if (entity.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already marked SUCCESS for orderId={}", entity.getOrderId());
            return;
        }

        entity.setGatewayPaymentId(gatewayPaymentId);
        entity.setStatus(newStatus);
        entity.setUpdatedAt(LocalDateTime.now());

        repo.save(entity);
        log.info("Payment status updated: orderId={}, newStatus={}", entity.getOrderId(), newStatus);

        try {
            if (newStatus == PaymentStatus.SUCCESS) {
                createSuccessEvent(entity, gatewayPaymentId);
                log.info("Payment success event saved in outbox for orderId={}", entity.getOrderId());
            } else if (newStatus == PaymentStatus.FAILED) {
                createFailureEvent(entity);
                log.info("Payment failure event saved in outbox for orderId={}", entity.getOrderId());
            }
        } catch (Exception e) {
            log.error("Failed to create outbox event for orderId={}", entity.getOrderId(), e);
            throw new PaymentEventCreationException("Failed to create outbox event", e);
        }
    }

    private PaymentStatus mapStatusByEvent(String eventType) {
        return switch (eventType.toLowerCase()) {
            case "payment.captured" -> PaymentStatus.SUCCESS;
            case "payment.failed" -> PaymentStatus.FAILED;
            case "payment.authorized" -> PaymentStatus.PENDING;
            default -> PaymentStatus.PENDING;
        };
    }

    private void createSuccessEvent(PayementEntity entity, String gatewayPaymentId) throws Exception {
        String eventId = UUID.randomUUID().toString();

        PaymentSuccessEvent payload = new PaymentSuccessEvent();
        payload.setOrderId(entity.getOrderId());
        payload.setUserId(entity.getUserId());
        payload.setAmount(entity.getAmount());
        payload.setPaymentTransactionId(gatewayPaymentId);

        DomainEvent<PaymentSuccessEvent> event = new DomainEvent<>(
                eventId,
                "PAYMENT_SUCCESS",
                LocalDateTime.now(),
                payload
        );

        saveOutboxEvent(eventId, entity, "PAYMENT_SUCCESS", event);
    }

    private void createFailureEvent(PayementEntity entity) throws Exception {
        String eventId = UUID.randomUUID().toString();

        PaymentFailedEvent payload = new PaymentFailedEvent();
        payload.setOrderId(entity.getOrderId());
        payload.setUserId(entity.getUserId());
        payload.setReason("Server error....");

        DomainEvent<PaymentFailedEvent> event = new DomainEvent<>(
                eventId,
                "PAYMENT_FAILED",
                LocalDateTime.now(),
                payload
        );

        saveOutboxEvent(eventId, entity, "PAYMENT_FAILED", event);
    }

    private void saveOutboxEvent(String eventId,
                                 PayementEntity entity,
                                 String eventType,
                                 DomainEvent<?> event) throws Exception {

        String payload = mapper.writeValueAsString(event);

        OutboxEntity outbox = new OutboxEntity(
                eventId,
                "PAYMENT",
                entity.getOrderId().toString(),
                eventType,
                payload,
                OutboxStatus.NEW
        );

        outboxRepo.save(outbox);
    }
}