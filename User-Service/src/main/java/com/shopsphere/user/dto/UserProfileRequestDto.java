package com.shopsphere.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProfileRequestDto {
	   @NotBlank
	    private String fullName;

	    @NotBlank
	    private String phno;

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
