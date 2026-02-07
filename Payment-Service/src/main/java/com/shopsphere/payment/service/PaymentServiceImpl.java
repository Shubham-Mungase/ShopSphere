package com.shopsphere.payment.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.shopsphere.payment.dto.PaymentRequestDto;
import com.shopsphere.payment.dto.PaymentResponseDto;
import com.shopsphere.payment.entity.PayementEntity;
import com.shopsphere.payment.enums.PaymentStatus;
import com.shopsphere.payment.repo.PaymentRepo;

@Service
public class PaymentServiceImpl implements PaymentService {

    //private final WebHookRest webHookRest;
    
    @Value("${razor.pay.key}")
    private String razorPayKey;
    @Value("${razor.pay.secret}")
    private String razorPaySecret;

	private final PaymentRepo repo;
	
	private  RazorpayClient razorpayClient;

	public PaymentServiceImpl(PaymentRepo repo){
		super();
		this.repo = repo;
		}

	@Override
	public PaymentResponseDto createPayment(PaymentRequestDto request) {

	    Optional<PayementEntity> existingPayment =
	            repo.findByOrderId(request.getOrderId());

	    if (existingPayment.isPresent()) {
	        return mapToDto(existingPayment.get());
	    }

	    PayementEntity entity = new PayementEntity();
	    entity.setAmount(request.getAmount());
	    entity.setOrderId(request.getOrderId());
	    entity.setUserId(request.getUserId());
	    entity.setPaymentMethod("ONLINE");
	    entity.setStatus(PaymentStatus.INITIATED);
	    
	    

	    PayementEntity savedEntity = repo.save(entity);

	    JSONObject options = new JSONObject();
	    options.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)));
	    options.put("currency", "INR");
	    options.put("receipt", savedEntity.getId().toString());
System.out.println("hello");


	    try {
	    	razorpayClient = new RazorpayClient(razorPayKey, razorPaySecret);
	        Order order = razorpayClient.orders.create(options);
	        savedEntity.setGatewayOrderId(order.get("id"));
	        System.out.println("Order Id"+order.get("id"));
	        entity.setStatus(PaymentStatus.CREATED);
	        repo.save(savedEntity);

	    } catch (Exception e) {
	        savedEntity.setStatus(PaymentStatus.FAILED);
	        repo.save(savedEntity);
	        System.out.println("Razorpay failed: " + e.getMessage());
	        return mapToDto(savedEntity); // DO NOT throw
	    }

	    return mapToDto(savedEntity);
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
