package com.famtwen.notification.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. Notification Template Document
@Document(collection = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplate {
    @Id
    String id;

    @Indexed(unique = true)
    String templateCode; // welcome, otp, order_confirmation, password_reset, etc.

    String channel; // EMAIL, SMS, PUSH
    String subject;
    String htmlContent;
    String textContent;

    @Builder.Default
    List<String> requiredParams = new ArrayList<>(); // ["userName", "verificationCode", etc.]

    @Builder.Default
    Map<String, Object> defaultParams = new HashMap<>();

    String description;
    boolean active;

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;
}