package com.shopsphere.shipping.dto.response;

import java.util.UUID;

public class WarehouseResponse {
	 private UUID warehouseId;
	    private String warehouseName;
	    private String location;
	    private Integer availableStock;
		public WarehouseResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public WarehouseResponse(UUID warehouseId, String warehouseName, String location, Integer availableStock) {
			super();
			this.warehouseId = warehouseId;
			this.warehouseName = warehouseName;
			this.location = location;
			this.availableStock = availableStock;
		}
		public UUID getWarehouseId() {
			return warehouseId;
		}
		public void setWarehouseId(UUID warehouseId) {
			this.warehouseId = warehouseId;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public Integer getAvailableStock() {
			return availableStock;
		}
		public void setAvailableStock(Integer availableStock) {
			this.availableStock = availableStock;
		}

}
