package com.shopsphere.notification.serviceimpl;

import org.springframework.stereotype.Service;

import com.shopsphere.notification.dto.NotificationRequest;
import com.shopsphere.notification.entity.NotificationEntity;
import com.shopsphere.notification.enums.NotificationStatus;
import com.shopsphere.notification.repo.NotificationRepo;
import com.shopsphere.notification.service.NotificationService;
import com.shopsphere.notification.utils.EmailService;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepo repo;

	private final EmailService emailService;

	public NotificationServiceImpl(NotificationRepo repo, EmailService emailService) {
		super();
		this.repo = repo;
		this.emailService = emailService;
	}

	@Override
	public String createNotification(NotificationRequest request) {

		NotificationEntity entity = new NotificationEntity();

		entity.setMessage(request.getMessage());
		entity.setUserId(request.getUserId());
		entity.setEventType(request.getEventType());
		
		try {

			emailService.sendEmail(request.getEmail(), request.getSubject(), request.getMessage());

			entity.setStatus(NotificationStatus.SENT);
		} catch (Exception e) {
			// TODO: handle exception
			entity.setStatus(NotificationStatus.FAILED);
		}

		NotificationEntity save = repo.save(entity);

		return save.getId().toString();
	}

}
