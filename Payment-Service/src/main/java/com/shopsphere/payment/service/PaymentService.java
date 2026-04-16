package com.shopsphere.payment.service;

import java.util.UUID;

import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.dto.PaymentResponseDto;

public interface PaymentService {

    void createPendingPayment(PaymentRequestDto request);

    PaymentResponseDto createPayment(UUID orderId,UUID userId);

    PaymentResponseDto getPayment(UUID paymentId);

}
