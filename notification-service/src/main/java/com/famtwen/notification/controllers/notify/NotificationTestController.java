package com.famtwen.notification.controllers.notify;

import com.famtwen.notification.dto.ApiResponse;
import com.famtwen.notification.dto.notify.*;
import com.famtwen.notification.services.NotificationExampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

// 4. Controller để test các template
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class NotificationTestController {
    private final NotificationExampleService notificationService;

    @PostMapping("/welcome")
    public ApiResponse<String> testWelcomeEmail(@RequestBody TestWelcomeRequest request) {
        notificationService.sendWelcomeEmail(request.getEmail(), request.getName());
        return ApiResponse.<String>builder()
                          .result("Welcome email sent successfully")
                          .build();
    }

    @PostMapping("/otp")
    public ApiResponse<String> testOtpEmail(@RequestBody TestOtpRequest request) {
        // Generate random OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        notificationService.sendOtpEmail(request.getEmail(), request.getName(), otpCode);
        return ApiResponse.<String>builder()
                          .result("OTP email sent successfully with code: " + otpCode)
                          .build();
    }

    @PostMapping("/order-confirmation")
    public ApiResponse<String> testOrderConfirmation(@RequestBody TestOrderRequest request) {
        OrderInfo orderInfo = OrderInfo.builder()
                                       .customerId("CUST001")
                                       .customerEmail(request.getEmail())
                                       .customerName(request.getName())
                                       .orderNumber("ORD" + System.currentTimeMillis())
                                       .orderDate(LocalDateTime.now())
                                       .totalAmount(request.getTotalAmount())
                                       .shippingAddress(request.getShippingAddress())
                                       .paymentMethod(request.getPaymentMethod())
                                       .build();

        notificationService.sendOrderConfirmation(orderInfo);
        return ApiResponse.<String>builder()
                          .result("Order confirmation email sent successfully")
                          .build();
    }

    @PostMapping("/password-reset")
    public ApiResponse<String> testPasswordReset(@RequestBody TestPasswordResetRequest request) {
        String resetToken = UUID.randomUUID().toString();
        notificationService.sendPasswordReset(request.getEmail(), request.getName(), resetToken);
        return ApiResponse.<String>builder()
                          .result("Password reset email sent successfully")
                          .build();
    }

    @PostMapping("/promotion")
    public ApiResponse<String> testPromotion(@RequestBody TestPromotionRequest request) {
        PromotionInfo promotion = PromotionInfo.builder()
                                               .promotionId("PROMO001")
                                               .title("Flash Sale 50%")
                                               .description("Siêu sale cuối tuần - Giảm giá sốc tất cả sản phẩm!")
                                               .discountPercent(50)
                                               .promoCode("FLASH50")
                                               .minAmount(new BigDecimal("200000"))
                                               .startDate(LocalDateTime.now())
                                               .endDate(LocalDateTime.now().plusDays(3))
                                               .build();

        notificationService.sendPromotionEmail(request.getEmail(), request.getName(), promotion);
        return ApiResponse.<String>builder()
                          .result("Promotion email sent successfully")
                          .build();
    }
}