package com.shopsphere.cart.event.publisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.cart.constants.AppConstants;
import com.shopsphere.cart.document.OutboxEvent;
import com.shopsphere.cart.enums.OutboxStatus;
import com.shopsphere.cart.exceptions.EventPublishException;
import com.shopsphere.cart.repo.OutboxRepository;

@Component
@Transactional
public class CartEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CartEventPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CartEventPublisher(OutboxRepository outboxRepository,
                             KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        log.info("Starting outbox event publishing job...");

        List<OutboxEvent> events = outboxRepository.findByStatus(OutboxStatus.NEW);

        if (events.isEmpty()) {
            log.info("No new events found in outbox");
            return;
        }

        log.info("Found {} new events to publish", events.size());

        for (OutboxEvent event : events) {

            try {

                log.info("Publishing event: id={}, type={}",
                        event.getId(), event.getEventType());

                kafkaTemplate.send(AppConstants.TOPIC, event.getPayload())
                        .whenComplete((result, ex) -> {

                            if (ex == null) {

                                log.info("Event published successfully: id={}", event.getId());

                                event.setStatus(OutboxStatus.SENT);
                                outboxRepository.save(event);

                            } else {

                                log.error("Failed to publish event: id={}", event.getId(), ex);

                                event.setStatus(OutboxStatus.FAILED);
                                outboxRepository.save(event);
                            }
                        });

            } catch (Exception e) {

                log.error("Unexpected error while publishing event: id={}", event.getId(), e);

                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);

                // Optional: rethrow if you want retry mechanisms outside
                 throw new EventPublishException("Failed to publish event", e);
            }
        }

        log.info("Outbox event publishing job completed");
    }
}