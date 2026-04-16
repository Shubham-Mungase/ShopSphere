package com.shopsphere.auth.dto.event;

public class UserRegisteredEvent extends BaseEvent {
    private String email;
    private String name;
    private String purpose;
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
}