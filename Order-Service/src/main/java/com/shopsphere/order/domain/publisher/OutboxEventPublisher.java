package com.shopsphere.order.domain.publisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shopsphere.order.domain.constants.AppConstants;
import com.shopsphere.order.domain.entity.OutboxEntity;
import com.shopsphere.order.domain.repo.OutboxRepository;

@Component
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepo;

    public OutboxEventPublisher(KafkaTemplate<String, String> kafkaTemplate, OutboxRepository outboxRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepo = outboxRepo;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvent() {
        log.info("Scheduled OutboxEventPublisher started");

        List<OutboxEntity> list = outboxRepo.findByStatus("NEW");
        log.info("Found {} new outbox events to publish", list.size());

        for (OutboxEntity event : list) {
            try {
                log.debug("Publishing eventId={} to topic={}", event.getEventId(), AppConstants.TOPIC);

                kafkaTemplate.send(AppConstants.TOPIC, event.getEventId(), event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send eventId={} to Kafka", event.getEventId(), ex);
                                event.setStatus("FAILED");
                                outboxRepo.save(event);
                            } else {
                                log.info("Event published successfully: eventId={}", event.getEventId());
                                event.setStatus("SENT");
                                outboxRepo.save(event);
                            }
                        });
            } catch (Exception e) {
                log.error("Exception while publishing eventId={}", event.getEventId(), e);
                event.setStatus("FAILED");
                outboxRepo.save(event);
            }
        }
    }
    
}