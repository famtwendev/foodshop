package com.famtwen.identity_service.dto.request.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyCloakError {
    String errorMessage;
}
