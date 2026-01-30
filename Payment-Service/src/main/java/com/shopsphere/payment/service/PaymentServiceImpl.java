package com.shopsphere.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.dto.PaymentResponseDto;
import com.shopsphere.payment.entity.PayementEntity;
import com.shopsphere.payment.enums.PaymentStatus;
import com.shopsphere.payment.repo.PaymentRepo;
import com.shopsphere.payment.rest.WebHookRest;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final WebHookRest webHookRest;

	private final PaymentRepo repo;

	public PaymentServiceImpl(PaymentRepo repo, WebHookRest webHookRest) {
		super();
		this.repo = repo;
		this.webHookRest = webHookRest;
	}

	@Override
	public PaymentResponseDto createPayment(PaymentRequestDto request) {
		repo.findByOrderId(request.getOrderId()).ifPresent(p -> {
			throw new RuntimeException("Payment Already exist for this order");
		});

		PayementEntity entity = new PayementEntity();

		entity.setAmount(request.getAmount());
		entity.setOrderId(request.getOrderId());
		entity.setUserId(request.getUserId());
		entity.setPaymentMethod("ONLINE");
		entity.setStatus(PaymentStatus.CREATED);
		PayementEntity payementEntity = repo.save(entity);
		
		System.out.println(payementEntity.toString());
		return mapToDto(payementEntity);
	}

	private PaymentResponseDto mapToDto(PayementEntity payementEntity) {
		PaymentResponseDto dto = new PaymentResponseDto();
		dto.setAmount(payementEntity.getAmount());
		dto.setOrderId(payementEntity.getOrderId());
		dto.setStatus(payementEntity.getStatus());
		dto.setUserId(payementEntity.getUserId());
		dto.setPaymentId(payementEntity.getId());
		return dto;
	}
	@Override
	public PaymentResponseDto getPayment(UUID paymentId) {
		
		 PayementEntity payementEntity = repo.findById(paymentId) .orElseThrow(() -> new RuntimeException("Payment not found"));
			return mapToDto(payementEntity);
	}

}
