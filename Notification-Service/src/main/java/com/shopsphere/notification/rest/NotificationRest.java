package com.shopsphere.notification.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.notification.dto.NotificationRequest;
import com.shopsphere.notification.service.NotificationService;


@RestController
@RequestMapping("/api/notifications")
public class NotificationRest {
	
	  private final NotificationService notificationService;
	  

	    public NotificationRest(NotificationService notificationService) {
		super();
		this.notificationService = notificationService;
	}


		// ✅ Send Notification
	    @PostMapping
	    public ResponseEntity<String> sendNotification(
	            @RequestBody NotificationRequest request) {

	        String saved = notificationService.createNotification(request);
	        return ResponseEntity.ok(saved);
	    }

}
