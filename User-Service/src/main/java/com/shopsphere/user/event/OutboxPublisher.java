package com.shopsphere.user.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shopsphere.user.entity.OutboxEvent;
import com.shopsphere.user.repo.OutboxRepository;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events = outboxRepository.findByStatus("PENDING");

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        "user-events",
                        event.getAggregateId(),
                        event.getPayload()
                );

                event.setStatus("SENT");

            } catch (Exception e) {
                event.setStatus("FAILED");
            }

            outboxRepository.save(event);
        }
    }
}