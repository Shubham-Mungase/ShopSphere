package com.shopsphere.shipping.event.publisher;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.shipping.constants.AppConstants;
import com.shopsphere.shipping.entity.OutboxEvent;
import com.shopsphere.shipping.repo.OutboxRepository;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 🔥 Runs every 5 seconds
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxRepository.findTop10ByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEvent event : events) {

            try {
                log.info("Publishing event type={} aggregateId={}",
                        event.getEventType(), event.getAggregateId());

                //  Send to Kafka
                kafkaTemplate.send(
                		AppConstants.TOPIC,
                		event.getAggregateId().toString(),
                        event.getPayload()
                );

                //  Mark as SENT
                event.setStatus("SENT");
                event.setProcessedAt(LocalDateTime.now());

            } catch (Exception ex) {

                log.error("Failed to publish event {}", event.getId(), ex);

                event.setStatus("FAILED");
            }
        }
    }

    
}