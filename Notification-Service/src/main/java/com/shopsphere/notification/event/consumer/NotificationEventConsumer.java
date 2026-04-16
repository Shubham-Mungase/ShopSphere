package com.shopsphere.notification.event.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.notification.consumer.dto.OtpGeneratedEvent;
import com.shopsphere.notification.consumer.dto.PasswordChangedEvent;
import com.shopsphere.notification.consumer.dto.UserEvent;
import com.shopsphere.notification.consumer.dto.UserRegisteredEvent;
@Component
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final HandleConsumeEvents handleEvents;

    public NotificationEventConsumer(ObjectMapper objectMapper, HandleConsumeEvents handleEvents) {
        this.objectMapper = objectMapper;
        this.handleEvents = handleEvents;
    }

    @KafkaListener(
        topics = "auth-events",
        groupId = "notification-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            JsonNode purposeNode = jsonNode.get("purpose");
            if (purposeNode == null) {
                logger.warn("Purpose field missing in payload: {}", payload);
                return;
            }

            String purpose = purposeNode.asText();
            logger.info("Event Purpose: {}", purpose);

            switch (purpose) {

                case "USER_REGISTERED":
                    UserRegisteredEvent userEvent =
                        objectMapper.treeToValue(jsonNode, UserRegisteredEvent.class);
                    handleEvents.handleUserRegistered(userEvent);
                    break;

                case "PASSWORD_CHANGED":
                    PasswordChangedEvent passEvent =
                        objectMapper.treeToValue(jsonNode, PasswordChangedEvent.class);
                    handleEvents.handlePasswordChanged(passEvent);
                    break;

                case "RESET_PASSWORD":
                    OtpGeneratedEvent otpEvent =
                        objectMapper.treeToValue(jsonNode, OtpGeneratedEvent.class);
                    handleEvents.handleOtp(otpEvent);
                    break;

                default:
                    logger.warn("Unknown purpose: {}", purpose);
            }

        } catch (Exception e) {
            logger.error("Error processing auth event: {}", payload, e);
        }
    }

    @KafkaListener(
        topics = "user-events",
        groupId = "user-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvents(String payload) {
        try {
            UserEvent value = objectMapper.readValue(payload, UserEvent.class);
            handleEvents.handleUserProfile(value);

        } catch (JsonProcessingException e) {
            logger.error("Error processing user event: {}", payload, e);
        }
    }
}