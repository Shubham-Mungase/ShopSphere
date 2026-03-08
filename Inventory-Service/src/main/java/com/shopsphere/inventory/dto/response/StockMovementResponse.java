package com.shopsphere.inventory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.inventory.enums.MovementType;

public class StockMovementResponse {

	 private UUID productId;
	    private MovementType type;
	    private Integer quantity;
	    private UUID orderId;
	    private String reason;
	    private LocalDateTime createdAt;
		public UUID getProductId() {
			return productId;
		}
		public void setProductId(UUID productId) {
			this.productId = productId;
		}
		public MovementType getType() {
			return type;
		}
		public void setType(MovementType type) {
			this.type = type;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public UUID getOrderId() {
			return orderId;
		}
		public void setOrderId(UUID orderId) {
			this.orderId = orderId;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
	    
	    
}
