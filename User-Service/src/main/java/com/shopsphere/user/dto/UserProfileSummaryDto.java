package com.shopsphere.user.dto;

import java.util.UUID;

public class UserProfileSummaryDto {
	
	 private UUID userId;
	    private String fullName;
	    private String phno;
		public UUID getUserId() {
			return userId;
		}
		public void setUserId(UUID userId) {
			this.userId = userId;
		}
		public String getFullName() {
			return fullName;
		}
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		public String getPhno() {
			return phno;
		}
		public void setPhno(String phno) {
			this.phno = phno;
		}
		
	    
	    

}
