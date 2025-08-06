package com.famtwen.identity_service.dto.request;

// 1. DTO cho Introspection Request

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectionRequest {
    @NotBlank(message = "Token is required")
    private String token;
}
