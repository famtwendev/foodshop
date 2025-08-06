package com.famtwen.notification.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

// 6. Enhanced Notification Event
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String channel; // Fixed typo: chanel -> channel
    String recipient;
    String templateCode;

    @Builder.Default
    Map<String, Object> params = new HashMap<>();

    // Optional: Override template content
    String customSubject;
    String customBody;

    // For tracking
    String correlationId;
    String userId;

    @Builder.Default
    Map<String, Object> metadata = new HashMap<>();
}
