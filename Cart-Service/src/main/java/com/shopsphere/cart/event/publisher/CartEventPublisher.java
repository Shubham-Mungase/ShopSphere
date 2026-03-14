package com.shopsphere.cart.event.publisher;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.shopsphere.cart.document.OutboxEvent;
import com.shopsphere.cart.enums.OutboxStatus;
import com.shopsphere.cart.repo.OutboxRepository;

public class CartEventPublisher {

	  private final OutboxRepository outboxRepository;
	    private final KafkaTemplate<String, String> kafkaTemplate;

	    public CartEventPublisher(OutboxRepository outboxRepository,
	                           KafkaTemplate<String, String> kafkaTemplate) {
	        this.outboxRepository = outboxRepository;
	        this.kafkaTemplate = kafkaTemplate;
	    }

	    @Scheduled(fixedDelay = 5000)
	    public void publishEvents() {

	        List<OutboxEvent> events = outboxRepository.findByStatus(OutboxStatus.NEW);

	        for (OutboxEvent event : events) {

	            try {

	                kafkaTemplate.send("checkout-topic", event.getPayload());

	                event.setStatus(OutboxStatus.SENT);

	                outboxRepository.save(event);

	            } catch (Exception e) {

	                event.setStatus(OutboxStatus.FAILED);

	                outboxRepository.save(event);
	            }
	        }
	    }
	
}

