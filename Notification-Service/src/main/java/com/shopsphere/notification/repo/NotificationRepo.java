package com.shopsphere.notification.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.notification.entity.NotificationEntity;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, UUID>{

}
