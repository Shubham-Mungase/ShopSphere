package com.shopsphere.shipping.dto.event;

import java.util.UUID;

public class OrderItemDTO {

	 private UUID productId;
	    private int quantity;
		public UUID getProductId() {
			return productId;
		}
		public void setProductId(UUID productId) {
			this.productId = productId;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
	    
}
