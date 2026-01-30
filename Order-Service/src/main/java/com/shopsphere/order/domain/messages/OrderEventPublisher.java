package com.shopsphere.order.domain.messages;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.shopsphere.order.domain.constants.AppConstants;

@Component
public class OrderEventPublisher {
	
	private KafkaTemplate<String, Object> kafkaTemplate;

	public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publishEvent(OrderCreatedEvent createdEvent)
	{
		
		
		kafkaTemplate.send(AppConstants.TOPIC, createdEvent.getOrderId().toString(), createdEvent);
		System.out.println("Event is publicshing");
	}
	
	

}
