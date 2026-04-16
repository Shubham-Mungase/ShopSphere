package com.shopsphere.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.dto.PaymentResponseDto;
import com.shopsphere.payment.entity.PayementEntity;
import com.shopsphere.payment.enums.PaymentStatus;
import com.shopsphere.payment.exception.PaymentGatewayException;
import com.shopsphere.payment.exception.PaymentNotFoundException;
import com.shopsphere.payment.exception.PaymentOwnershipException;
import com.shopsphere.payment.repo.PaymentRepo;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

	private final PaymentRepo repo;
	private final RazorpayClient razorpayClient;

	public PaymentServiceImpl(PaymentRepo repo, RazorpayClient razorpayClient) {
		this.repo = repo;
		this.razorpayClient = razorpayClient;
	}

	@Override
	public void createPendingPayment(PaymentRequestDto request) {
		if (repo.findByOrderId(request.getOrderId()).isPresent()) {
			log.info("Pending payment already exists for orderId={}", request.getOrderId());
			return; // idempotent
		}

		PayementEntity entity = new PayementEntity();
		entity.setOrderId(request.getOrderId());
		entity.setUserId(request.getUserId());
		entity.setAmount(request.getAmount());
		entity.setPaymentMethod("ONLINE");
		entity.setStatus(PaymentStatus.INITIATED);
		entity.setCreatedAt(LocalDateTime.now());

		repo.save(entity);
		log.info("Created pending payment for orderId={}, userId={}", request.getOrderId(), request.getUserId());
	}

	@Override
	@Transactional
	public PaymentResponseDto createPayment(UUID orderId, UUID userId) {

		PayementEntity entity = repo.findByOrderId(orderId)
				.orElseThrow(() -> new PaymentNotFoundException("Payment not found for orderId=" + orderId));

		if (!entity.getUserId().equals(userId)) {
			log.warn("User {} attempted to pay for order {} not belonging to them", userId, orderId);
			throw new PaymentOwnershipException("Not your order");
		}

		if (entity.getStatus() == PaymentStatus.CREATED || entity.getStatus() == PaymentStatus.SUCCESS) {
			log.info("Payment already processed or created for orderId={}", orderId);
			return map(entity);
		}

		JSONObject options = new JSONObject();
		options.put("amount", entity.getAmount().multiply(BigDecimal.valueOf(100)));
		options.put("currency", "INR");
		options.put("receipt", orderId.toString());

		try {
			Order order = razorpayClient.orders.create(options);

			entity.setGatewayOrderId(order.get("id"));
			entity.setStatus(PaymentStatus.CREATED);
			entity.setUpdatedAt(LocalDateTime.now());

			log.info("Razorpay order created successfully for orderId={} gatewayOrderId={}", orderId, order.get("id"));

		} catch (Exception e) {
			entity.setStatus(PaymentStatus.FAILED);
			entity.setUpdatedAt(LocalDateTime.now());

			log.error("Payment creation failed for orderId={}", orderId, e);
			throw new PaymentGatewayException("Failed to create payment for orderId=" + orderId, e);
		}

		repo.save(entity);
		return map(entity);
	}

	@Override
	public PaymentResponseDto getPayment(UUID id) {
		return map(
				repo.findById(id).orElseThrow(() -> new PaymentNotFoundException("Payment not found with id=" + id)));
	}

	private PaymentResponseDto map(PayementEntity e) {
		PaymentResponseDto dto = new PaymentResponseDto();
		dto.setPaymentId(e.getId());
		dto.setOrderId(e.getOrderId());
		dto.setUserId(e.getUserId());
		dto.setAmount(e.getAmount());
		dto.setStatus(e.getStatus());
		dto.setGatewayOrderId(e.getGatewayOrderId());
		return dto;
	}
}