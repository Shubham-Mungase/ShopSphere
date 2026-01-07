package com.shopsphere.product.dto;

import java.math.BigDecimal;

public class ProductSnapshotDto {
	 private String productName;
	    private BigDecimal finalPrice;
	    private String imageUrl;
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
