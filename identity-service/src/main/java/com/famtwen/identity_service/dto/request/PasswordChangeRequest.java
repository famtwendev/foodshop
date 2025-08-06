package com.famtwen.identity_service.dto.request;

import com.famtwen.identity_service.validators.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@PasswordMatches
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordChangeRequest {
    @Size(min = 6, message = "INVALID_PASSWORD")
    @NotBlank(message = "PASSWORD_IS_REQUIRED")
    String password;

    @Size(min = 6, message = "INVALID_PASSWORD")
    @NotBlank(message = "RETYPEPASSWORD_IS_REQUIRED")
    String retypepassword;
}
