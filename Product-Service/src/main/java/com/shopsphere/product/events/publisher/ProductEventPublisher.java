package com.shopsphere.product.events.publisher;

import java.util.List;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.product.constants.AppConstants;
import com.shopsphere.product.entity.OutboxEntity;
import com.shopsphere.product.repo.OutboxRepository;

@Service
public class ProductEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                 OutboxRepository outboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
    }

    // 🔥 Run every 5 sec
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {

        // ✅ Pagination (avoid memory issues)
        List<OutboxEntity> events = outboxRepository.findByStatus("NEW");

        if (events.isEmpty()) {
            return;
        }

        for (OutboxEntity event : events) {

            try {
                log.info("Publishing event: id={}, aggregateId={}",
                        event.getId(), event.getAggregateId());

                kafkaTemplate.send(
                        AppConstants.TOPIC,
                        event.getAggregateId(),   // partition key
                        event.getPayload()
                ).whenComplete((result, ex) -> {

                    if (ex == null) {
                        // ✅ Success
                        RecordMetadata metadata = result.getRecordMetadata();

                        log.info("Event sent successfully: topic={}, partition={}, offset={}",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset());

                        event.setStatus("SENT");
                        //event.setProcessedAt(LocalDateTime.now());

                    } else {
                        // ❌ Failure
                        log.error("Kafka send failed for eventId={}", event.getId(), ex);

                      //  event.setRetryCount(event.getRetryCount() + 1);

//                        if (event.getRetryCount() >= 3) {
//                            event.setStatus("FAILED");
//                        }
                    }

                    outboxRepository.save(event);
                });

            } catch (Exception e) {
                log.error("Unexpected error while publishing eventId={}", event.getId(), e);

               // event.setRetryCount(event.getRetryCount() + 1);

//                if (event.getRetryCount() >= 3) {
//                    event.setStatus("FAILED");
//                }

                outboxRepository.save(event);
            }
        }
    }
}