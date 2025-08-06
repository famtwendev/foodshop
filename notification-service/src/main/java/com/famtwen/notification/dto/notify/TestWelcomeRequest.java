package com.famtwen.notification.dto.notify;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
// 5. Test Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestWelcomeRequest {
    @Email
    String email;
    @NotBlank
    String name;
}
