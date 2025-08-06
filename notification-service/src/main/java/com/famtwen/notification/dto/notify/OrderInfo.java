package com.famtwen.notification.dto.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// 3. DTOs cho các ví dụ
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    String customerId;
    String customerEmail;
    String customerName;
    String orderNumber;
    LocalDateTime orderDate;
    BigDecimal totalAmount;
    String shippingAddress;
    String paymentMethod;
}