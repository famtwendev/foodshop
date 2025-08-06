package com.famtwen.notification.entity;

import com.famtwen.notification.exception.NotificationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// 2. Notification History Document
@Document(collection = "notification_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationHistory {
    @Id
    String id;

    String channel;
    String recipient;
    String templateCode;
    String subject;
    String content;

    @Builder.Default
    NotificationStatus status = NotificationStatus.PENDING;

    String errorMessage;
    String externalMessageId; // From email provider

    @Builder.Default
    Map<String, Object> metadata = new HashMap<>();

    @CreatedDate
    LocalDateTime createdAt;

    @LastModifiedDate
    LocalDateTime updatedAt;
}
