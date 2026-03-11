package com.shopsphere.notification.dto;

import java.util.UUID;

import com.shopsphere.notification.enums.NotificationStatus;


public class NotificationRequest {
	
	private UUID userId;

	private String eventType;

	private String message;

	private NotificationStatus status; // PENDING, SENT, FAILED
	private String email;
	private String subject;
	
	
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}



	
}
