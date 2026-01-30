package com.shopsphere.payment.consumer;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import com.shopsphere.payment.common.event.OrderEvents;
import com.shopsphere.payment.constants.AppConstants;
import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.service.PaymentService;

@Component
@EnableKafka
public class OrderEventConsumer {
	
	private final PaymentService paymentService;

	public OrderEventConsumer(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}
	
	@KafkaListener(topics =AppConstants.TOPIC,groupId = AppConstants.GROUP_ID,containerFactory = "kafkaListenerContainerFactory")
	public void consumerOrderCreatedEvent(OrderEvents event) {
		PaymentRequestDto dto=new PaymentRequestDto();
		
		System.out.println(" Order Event Received: " + event.getOrderId());
		
		dto.setOrderId(event.getOrderId());
		dto.setUserId(event.getUserId());
		dto.setAmount(event.getTotalAmount());
		
		paymentService.createPayment(dto);
		 
		  System.out.println("payment initiated with order Id : " + event.getOrderId());
	}
	

}
