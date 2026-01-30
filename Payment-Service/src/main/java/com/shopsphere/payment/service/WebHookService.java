package com.shopsphere.payment.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shopsphere.payment.dto.PaymentWebhookRequest;
import com.shopsphere.payment.entity.PayementEntity;
import com.shopsphere.payment.enums.PaymentStatus;
import com.shopsphere.payment.repo.PaymentRepo;
import com.shopsphere.payment.utils.WebhookSignatureVerifier;

import tools.jackson.databind.ObjectMapper;

@Service
public class WebHookService {

	private final WebhookSignatureVerifier verifier;
	private final ObjectMapper mapper;
	private final PaymentRepo repo;
	
	@Value("${payment.webhook.secret}")
	private String webHookSecret;
	
	public WebHookService(WebhookSignatureVerifier verifier, ObjectMapper mapper, PaymentRepo repo) {
		super();
		this.verifier = verifier;
		this.mapper = mapper;
		this.repo = repo;
	}
	
	
	public void processWebHook(String rawPayLoad,String signature)
	{
		boolean isValid = verifier.verify(rawPayLoad, signature, webHookSecret);
		
		if(!isValid)
		{
			throw new RuntimeException("Invalid WebHook Signature");
		}
		
		PaymentWebhookRequest request;
		
		try {
			 request = mapper.readValue(rawPayLoad, PaymentWebhookRequest.class);
			
		} catch (Exception e) {
			throw new RuntimeException("Invalid webhook payload="+e);
		}
//		
//		PayementEntity payementEntity = repo.findById(request.getPaymentId()).orElseThrow(()-> new RuntimeException("payment record not found"));
	
		String paymentId = request.getPaymentId();
		UUID uuid = UUID.fromString(paymentId);
		PayementEntity payementEntity = repo.findById(uuid).get();
		if (payementEntity.getStatus()==PaymentStatus.SUCCESS) {
			return;
		}
		
		payementEntity.setStatus(PaymentStatus.valueOf(request.getStatus()));
		payementEntity.setUpdatedAt(LocalDateTime.now());
		repo.save(payementEntity);
		
	}
	

}
