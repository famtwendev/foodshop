package com.famtwen.notification.services;

import com.famtwen.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;


// PUBLIC FOR ALL SERVICE IN PACKAGES
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendWelcomeNotification(String userId, String email, String name) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(email)
                                                   .templateCode("WELCOME")
                                                   .params(Map.of(
                                                           "userName", name,
                                                           "userEmail", email,
                                                           "activationLink", "https://myapp.com/activate?userId=" + userId,
                                                           "appName", "MyShop"
                                                   ))
                                                   .correlationId(UUID.randomUUID().toString())
                                                   .userId(userId)
                                                   .build();

        kafkaTemplate.send("notification-delivery", event);
        log.info("Sent welcome notification event for user: {}", email);
    }

    public void sendOtpNotification(String email, String name, String otpCode) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(email)
                                                   .templateCode("OTP_VERIFICATION")
                                                   .params(Map.of(
                                                           "userName", name,
                                                           "otpCode", otpCode,
                                                           "purpose", "xác thực đăng nhập"
                                                   ))
                                                   .correlationId(UUID.randomUUID().toString())
                                                   .build();

        kafkaTemplate.send("notification-delivery", event);
        log.info("Sent OTP notification event for user: {}", email);
    }
}
