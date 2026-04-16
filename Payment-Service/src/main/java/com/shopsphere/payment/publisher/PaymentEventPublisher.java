package com.shopsphere.payment.publisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.payment.entity.OutboxEntity;
import com.shopsphere.payment.enums.OutboxStatus;
import com.shopsphere.payment.repo.OutboxRepo;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final OutboxRepo outboxRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventPublisher(OutboxRepo outboxRepo, KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxRepo = outboxRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPaymentEvents() {
        log.info("Scheduled PaymentEventPublisher started");

        List<OutboxEntity> events = outboxRepo.findByStatus(OutboxStatus.NEW);
        log.info("Found {} new payment outbox events to publish", events.size());

        for (OutboxEntity event : events) {
            try {
                log.debug("Publishing eventId={} to topic=payment-events", event.getEventId());

                // Blocking send to ensure delivery
                kafkaTemplate.send("payment-events", event.getAggregateId(), event.getPayload()).get();

                event.setStatus(OutboxStatus.SENT);
                outboxRepo.save(event);

                log.info("Event published successfully: eventId={}", event.getEventId());

            } catch (Exception e) {
                log.error("Failed to publish eventId={} to Kafka. Marking as FAILED", event.getEventId(), e);
                event.setStatus(OutboxStatus.FAILED);
                outboxRepo.save(event);
            }
        }
    }
}