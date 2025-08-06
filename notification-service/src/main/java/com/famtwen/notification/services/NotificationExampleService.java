package com.famtwen.notification.services;

import com.famtwen.notification.event.NotificationEvent;
import com.famtwen.notification.dto.notify.OrderInfo;
import com.famtwen.notification.dto.notify.PromotionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// 2. Service để gửi notification với examples thực tế
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationExampleService {
    private final EmailService emailService;

    // Ví dụ 1: Gửi email chào mừng
    public void sendWelcomeEmail(String userEmail, String userName) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(userEmail)
                                                   .templateCode("WELCOME")
                                                   .params(Map.of(
                                                           "userName", userName,
                                                           "userEmail", userEmail,
                                                           "activationLink", "https://myapp.com/activate?token=abc123",
                                                           "appName", "MyShop"
                                                   ))
                                                   .correlationId(UUID.randomUUID().toString())
                                                   .userId("user123")
                                                   .build();

        emailService.sendTemplatedEmail(event);
    }

    // Ví dụ 2: Gửi mã OTP
    public void sendOtpEmail(String userEmail, String userName, String otpCode) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(userEmail)
                                                   .templateCode("OTP_VERIFICATION")
                                                   .params(Map.of(
                                                           "userName", userName,
                                                           "otpCode", otpCode,
                                                           "purpose", "đăng nhập vào hệ thống",
                                                           "expiryMinutes", "10"
                                                   ))
                                                   .correlationId(UUID.randomUUID().toString())
                                                   .build();

        emailService.sendTemplatedEmail(event);
    }

    // Ví dụ 3: Xác nhận đơn hàng
    public void sendOrderConfirmation(OrderInfo orderInfo) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(orderInfo.getCustomerEmail())
                                                   .templateCode("ORDER_CONFIRMATION")
                                                   .params(Map.of(
                                                           "customerName", orderInfo.getCustomerName(),
                                                           "orderNumber", orderInfo.getOrderNumber(),
                                                           "orderDate", orderInfo.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                                           "totalAmount", NumberFormat.getInstance(new Locale("vi", "VN")).format(orderInfo.getTotalAmount()),
                                                           "shippingAddress", orderInfo.getShippingAddress(),
                                                           "paymentMethod", orderInfo.getPaymentMethod(),
                                                           "trackingUrl", "https://myapp.com/orders/" + orderInfo.getOrderNumber()
                                                   ))
                                                   .correlationId(orderInfo.getOrderNumber())
                                                   .userId(orderInfo.getCustomerId())
                                                   .build();

        emailService.sendTemplatedEmail(event);
    }

    // Ví dụ 4: Đặt lại mật khẩu
    public void sendPasswordReset(String userEmail, String userName, String resetToken) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(userEmail)
                                                   .templateCode("PASSWORD_RESET")
                                                   .params(Map.of(
                                                           "userName", userName,
                                                           "resetLink", "https://myapp.com/reset-password?token=" + resetToken,
                                                           "expiryHours", "2"
                                                   ))
                                                   .correlationId(UUID.randomUUID().toString())
                                                   .build();

        emailService.sendTemplatedEmail(event);
    }

    // Ví dụ 5: Email khuyến mãi
    public void sendPromotionEmail(String customerEmail, String customerName, PromotionInfo promotion) {
        NotificationEvent event = NotificationEvent.builder()
                                                   .channel("EMAIL")
                                                   .recipient(customerEmail)
                                                   .templateCode("PROMOTION")
                                                   .params(Map.of(
                                                           "customerName", customerName,
                                                           "promotionTitle", promotion.getTitle(),
                                                           "promotionDescription", promotion.getDescription(),
                                                           "discountPercent", promotion.getDiscountPercent().toString(),
                                                           "promoCode", promotion.getPromoCode(),
                                                           "minAmount", NumberFormat.getInstance(new Locale("vi", "VN")).format(promotion.getMinAmount()),
                                                           "startDate", promotion.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                                           "endDate", promotion.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                   ))
                                                   .correlationId(promotion.getPromotionId())
                                                   .build();

        emailService.sendTemplatedEmail(event);
    }
}