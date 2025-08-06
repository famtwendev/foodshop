package com.famtwen.notification.dto.notify;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderRequest {
    @Email
    String email;
    @NotBlank
    String name;
    @NotNull
    BigDecimal totalAmount;
    @NotBlank
    String shippingAddress;
    String paymentMethod = "COD";
}