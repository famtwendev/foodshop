package com.famtwen.notification.dto.notify;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 10. Create Template Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTemplateRequest {
    @NotBlank
    String templateCode;

    @NotBlank
    String channel;

    @NotBlank
    String subject;

    @NotBlank
    String htmlContent;

    String textContent;
    String description;

    @Builder.Default
    List<String> requiredParams = new ArrayList<>();

    @Builder.Default
    Map<String, Object> defaultParams = new HashMap<>();
}