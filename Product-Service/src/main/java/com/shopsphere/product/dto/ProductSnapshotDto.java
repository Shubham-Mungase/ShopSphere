package com.shopsphere.product.dto;

import java.math.BigDecimal;

public class ProductSnapshotDto {
	private String productId;
	 private String productName;
	    private BigDecimal finalPrice;
	    private String imageUrl;
	    private BigDecimal price;
	    private BigDecimal discount;
	    
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public BigDecimal getDiscount() {
			return discount;
		}
		public void setDiscount(BigDecimal discount) {
			this.discount = discount;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public BigDecimal getFinalPrice() {
			return finalPrice;
		}
		public void setFinalPrice(BigDecimal finalPrice) {
			this.finalPrice = finalPrice;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
	    

}
