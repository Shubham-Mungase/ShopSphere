package com.shopsphere.payment.dto;

import java.math.BigDecimal;

public class PaymentWebhookRequest {

    private String paymentId;
    private String orderId;
    private String status;
    private BigDecimal amount;
    private String gatewayPaymentId;
	private String gatewayOrderId;
	
	
	public String getGatewayPaymentId() {
		return gatewayPaymentId;
	}
	public void setGatewayPaymentId(String gatewayPaymentId) {
		this.gatewayPaymentId = gatewayPaymentId;
	}
	public String getGatewayOrderId() {
		return gatewayOrderId;
	}
	public void setGatewayOrderId(String gatewayOrderId) {
		this.gatewayOrderId = gatewayOrderId;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

  
    
    
}
