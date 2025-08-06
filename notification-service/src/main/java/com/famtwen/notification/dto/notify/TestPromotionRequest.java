package com.famtwen.notification.dto.notify;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPromotionRequest {
    @Email
    String email;
    @NotBlank
    String name;
}
