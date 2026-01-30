package com.shopsphere.payment.service;

import java.util.UUID;

import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.dto.PaymentResponseDto;

public interface PaymentService {

	public PaymentResponseDto createPayment(PaymentRequestDto request);
	public PaymentResponseDto getPayment(UUID paymentId);
}
