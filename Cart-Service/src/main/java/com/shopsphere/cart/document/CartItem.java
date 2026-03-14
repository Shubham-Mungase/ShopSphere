package com.shopsphere.cart.document;

import java.util.UUID;

public class CartItem {
	  private UUID productId;

	    private String productName;

	    private Double price;

	    private Integer quantity;

		public UUID getProductId() {
			return productId;
		}

		public void setProductId(UUID productId) {
			this.productId = productId;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public CartItem(UUID productId, String productName, Double price, Integer quantity) {
			super();
			this.productId = productId;
			this.productName = productName;
			this.price = price;
			this.quantity = quantity;
		}

		public CartItem() {
			super();
			// TODO Auto-generated constructor stub
		}

	    
}
