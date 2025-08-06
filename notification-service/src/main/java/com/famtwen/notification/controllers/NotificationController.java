package com.famtwen.notification.controllers;

import com.famtwen.notification.event.NotificationEvent;
import com.famtwen.notification.services.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    EmailService emailService;

    @KafkaListener(topics = "notification-delivery", groupId = "notification-group")
    public void listenNotificationDelivery(NotificationEvent message) {
        log.info("Message received: {}", message);

        try {
            emailService.sendTemplatedEmail(message);
            log.info("Templated email sent successfully for templateCode: {}", message.getTemplateCode());
        } catch (Exception e) {
            log.error("Failed to send templated email: {}", e.getMessage(), e);
        }
    }
}
