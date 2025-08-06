package com.famtwen.identity_service.dto.request.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Credential {
    /*
     * Xác thực dùng để xác minh danh tính của người dùng
     * */
    String type;
    String value;
    boolean temporary;
}
