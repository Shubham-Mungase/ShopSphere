package com.shopsphere.payment.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.payment.dto.PaymentResponseDto;
import com.shopsphere.payment.service.PaymentService;

@Controller
@RequestMapping("/api/payments")
public class PaymentRest {

	private final PaymentService service;

	public PaymentRest(PaymentService service) {
		this.service = service;
	}

	@GetMapping("/index")
	public String init() {
		return "index";
	}

	// USER API (userId internal filter)
	@PostMapping("/{orderId}")
	public ResponseEntity<PaymentResponseDto> createPayment(@PathVariable UUID orderId, Authentication authentication) {

		String userId = (String) authentication.getDetails();

		return ResponseEntity.ok(service.createPayment(orderId, UUID.fromString(userId)));
	}

	// INTERNAL API (for other services)
	@GetMapping("/internal/{paymentId}")
	public ResponseEntity<PaymentResponseDto> getPaymentInternal(@PathVariable UUID paymentId) {
		return ResponseEntity.ok(service.getPayment(paymentId));
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable UUID paymentId) {
		return ResponseEntity.ok(service.getPayment(paymentId));
	}
}