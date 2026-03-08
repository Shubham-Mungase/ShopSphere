package com.shopsphere.inventory.dto.event;

import java.util.List;
import java.util.UUID;

public class StockReleasedEvent {

	 private UUID orderId;
	    private List<UUID> productIds;
		public UUID getOrderId() {
			return orderId;
		}
		public void setOrderId(UUID orderId) {
			this.orderId = orderId;
		}
		public List<UUID> getProductIds() {
			return productIds;
		}
		public void setProductIds(List<UUID> productIds) {
			this.productIds = productIds;
		}
		public StockReleasedEvent(UUID orderId, List<UUID> productIds) {
			super();
			this.orderId = orderId;
			this.productIds = productIds;
		}
		public StockReleasedEvent() {
			super();
			// TODO Auto-generated constructor stub
		}
	
}
