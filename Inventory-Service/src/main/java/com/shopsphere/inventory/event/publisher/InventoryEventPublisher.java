package com.shopsphere.inventory.event.publisher;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.inventory.constants.AppConstants;
import com.shopsphere.inventory.entity.OutboxEntity;
import com.shopsphere.inventory.enums.OutboxStatus;
import com.shopsphere.inventory.repo.OutboxRepo;

@Component
public class InventoryEventPublisher {

	private final OutboxRepo outboxRepo;
	 private final KafkaTemplate<String, Object> kafkaTemplate;
	public InventoryEventPublisher(OutboxRepo outboxRepo, KafkaTemplate<String, Object> kafkaTemplate) {
		super();
		this.outboxRepo = outboxRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedDelay = 5000)
	@Transactional
	public void publishInventoryEvent() {
		List<OutboxEntity> events = outboxRepo.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);
		for (OutboxEntity event : events) {
			
			try {

	            kafkaTemplate.send(
	                    AppConstants.TOPIC,
	                    event.getAggregateId(),
	                    event.getPayload()
	            ).get(); 

	            event.setStatus(OutboxStatus.SENT);

	        } catch (Exception ex) {

	            event.setStatus(OutboxStatus.FAILED);
	        }

		}
		
	}
	

}
