package com.shopsphere.inventory.dto.request;

import java.util.UUID;

public class AddStockRequest {
	    private UUID productId;

	    private Integer quantity;

	    private String reason;

		public UUID getProductId() {
			return productId;
		}

		public void setProductId(UUID productId) {
			this.productId = productId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

}
