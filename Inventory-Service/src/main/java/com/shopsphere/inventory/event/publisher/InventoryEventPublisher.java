package com.shopsphere.inventory.event.publisher;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shopsphere.inventory.constants.AppConstants;
import com.shopsphere.inventory.entity.OutboxEntity;
import com.shopsphere.inventory.enums.OutboxStatus;
import com.shopsphere.inventory.repo.OutboxRepo;

@Component
public class InventoryEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);

	private final OutboxRepo outboxRepo;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public InventoryEventPublisher(OutboxRepo outboxRepo, KafkaTemplate<String, Object> kafkaTemplate) {
		this.outboxRepo = outboxRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedDelay = 5000)
	public void publishInventoryEvent() {

		log.info("Starting InventoryEventPublisher job");

		List<OutboxEntity> events = outboxRepo.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

		log.info("Fetched {} NEW events from outbox", events.size());

		for (OutboxEntity event : events) {

			log.debug("Sending eventId={} asynchronously", event.getEventId());

			kafkaTemplate.send(AppConstants.TOPIC, event.getAggregateId(), event.getPayload())
					.whenComplete((result, ex) -> {

						if (ex != null) {

							log.error("Failed to publish eventId={}", event.getEventId(), ex);

							try {
								event.setStatus(OutboxStatus.FAILED);
								outboxRepo.save(event);
							} catch (Exception dbEx) {
								log.error("Failed to update FAILED status for eventId={}", event.getEventId(), dbEx);
							}

						} else {

							log.info("Event published successfully eventId={}", event.getEventId());

							try {
								event.setStatus(OutboxStatus.SENT);
								outboxRepo.save(event);
							} catch (Exception dbEx) {
								log.error("Failed to update SENT status for eventId={}", event.getEventId(), dbEx);
							}
						}
					});
		}

		log.info("InventoryEventPublisher job triggered successfully");
	}
}