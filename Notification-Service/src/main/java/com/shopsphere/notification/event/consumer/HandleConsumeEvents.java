package com.shopsphere.notification.event.consumer;

import org.springframework.stereotype.Component;

import com.shopsphere.notification.consumer.dto.OtpGeneratedEvent;
import com.shopsphere.notification.consumer.dto.PasswordChangedEvent;
import com.shopsphere.notification.consumer.dto.UserEvent;
import com.shopsphere.notification.consumer.dto.UserRegisteredEvent;
import com.shopsphere.notification.dto.NotificationRequest;
import com.shopsphere.notification.service.NotificationService;

@Component
public class HandleConsumeEvents {

	private NotificationService notificationService;

	public HandleConsumeEvents(NotificationService notificationService) {
		this.notificationService = notificationService;
	}


	protected void handlePasswordChanged(PasswordChangedEvent event) {

		NotificationRequest request = new NotificationRequest();

		request.setEmail(event.getEmail());
		request.setEventType(event.getPurpose());
		request.setSubject("Password Changed");
		request.setMessage("Your password has been successfully changed.");

		notificationService.createNotification(request);
	}

	protected void handleUserRegistered(UserRegisteredEvent event) {

		NotificationRequest request = new NotificationRequest();

		request.setEmail(event.getEmail());
		request.setEventType(event.getPurpose());
		request.setSubject("Welcome to ShopSphere 🎉");
		request.setMessage("Hello " + event.getName() + ", welcome to our platform!");

		notificationService.createNotification(request);
	}

	protected void handleOtp(OtpGeneratedEvent event) {

		NotificationRequest request = new NotificationRequest();

		request.setEmail(event.getEmail());
		request.setEventType(event.getPurpose());
		request.setSubject("OTP Verification");
		request.setMessage("Your OTP is: " + event.getOtp());

		notificationService.createNotification(request);
	}

	protected void handleUserProfile(UserEvent event) {

		NotificationRequest request = new NotificationRequest();

		request.setEmail(event.getEmail());
		request.setEventType("User_Events");
		request.setSubject("Profile Created");
		request.setMessage(String.format(
			    "Dear %s, your profile has been successfully created. You can now book orders.",
			    event.getName()
			));
		notificationService.createNotification(request);
	}

}
