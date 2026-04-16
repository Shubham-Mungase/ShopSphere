package com.shopsphere.auth.event.publisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shopsphere.auth.constants.AppConstants;
import com.shopsphere.auth.entity.OutboxEvent;
import com.shopsphere.auth.repo.OutboxRepository;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final OutboxRepository repo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(OutboxRepository repo, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repo = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void process() {

        List<OutboxEvent> events = repo.findTop50ByStatus("PENDING");

        if (events.isEmpty()) {
            log.debug("No pending outbox events found");
            return;
        }

        log.info("Processing {} outbox events", events.size());

        for (OutboxEvent event : events) {

            try {
                kafkaTemplate.send(AppConstants.TOPIC, event.getPayload())
                        .whenComplete((result, ex) -> {

                            if (ex == null) {
                                // SUCCESS
                                log.info("Event sent successfully. EventId={}, Topic={}, Partition={}, Offset={}",
                                        event.getId(),
                                        result.getRecordMetadata().topic(),
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());

                                event.setStatus("SENT");
                            } else {
                                log.error("Failed to send event. EventId={}, Error={}",
                                        event.getId(), ex.getMessage(), ex);

                                event.setStatus("FAILED");
                            }

                            // Save status update
                            repo.save(event);
                        });

            } catch (Exception e) {
                // Covers serialization errors, immediate failures
                log.error("Unexpected error while sending event. EventId={}, Error={}",
                        event.getId(), e.getMessage(), e);

                event.setStatus("FAILED");
                repo.save(event);
            }
        }
    }
}