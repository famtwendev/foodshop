package com.famtwen.notification.configuration;

import com.famtwen.notification.entity.NotificationTemplate;
import com.famtwen.notification.repository.NotificationTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateInitializer {
    private final NotificationTemplateRepository templateRepository;

    @PostConstruct
    public void initializeTemplates() {
        createWelcomeTemplate();
        createOtpTemplate();
        createOrderConfirmationTemplate();
        createPasswordResetTemplate();
        createPromotionTemplate();
        createLoginSuccessTemplate();
    }

    private void createWelcomeTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("WELCOME").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("WELCOME")
                                                            .channel("EMAIL")
                                                            .subject("Chào mừng {{userName}} đến với {{appName}}!")
                                                            .htmlContent("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Chào mừng</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; color: white; border-radius: 10px;">
                                <h1>Chào mừng bạn đến với {{appName}}!</h1>
                            </div>
                            
                            <div style="padding: 30px; background: #f9f9f9; margin: 20px 0;">
                                <h2>Xin chào {{userName}},</h2>
                                <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>{{appName}}</strong>!</p>
                                <p>Thông tin tài khoản của bạn:</p>
                                <ul>
                                    <li><strong>Email:</strong> {{userEmail}}</li>
                                    <li><strong>Ngày đăng ký:</strong> {{registrationDate}}</li>
                                </ul>
                                
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="{{activationLink}}" 
                                       style="background: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                                        Kích hoạt tài khoản
                                    </a>
                                </div>
                                
                                <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email: {{supportEmail}}</p>
                            </div>
                            
                            <div style="text-align: center; color: #666; font-size: 12px;">
                                <p>&copy; 2024 {{appName}}. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """)
                                                            .requiredParams(List.of("userName", "userEmail", "appName", "activationLink"))
                                                            .defaultParams(Map.of(
                                                                    "appName", "MyApp",
                                                                    "supportEmail", "support@myapp.com",
                                                                    "registrationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                            ))
                                                            .description("Welcome email template for new users")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created WELCOME template");
    }

    private void createLoginSuccessTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("LOGIN_SUCCESS").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("LOGIN_SUCCESS")
                                                            .channel("EMAIL")
                                                            .subject("Bạn đã đăng nhập thành công vào {{appName}}")
                                                            .htmlContent("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Đăng nhập thành công</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: #4CAF50; padding: 30px; text-align: center; color: white; border-radius: 10px;">
                        <h1>Xin chào {{userName}}</h1>
                        <p>Bạn vừa đăng nhập vào {{appName}} lúc {{loginTime}}</p>
                    </div>

                    <div style="padding: 30px; background: #f9f9f9; margin-top: 20px;">
                        <p>Nếu bạn không thực hiện hành động này, vui lòng liên hệ ngay với chúng tôi qua <strong>{{supportEmail}}</strong></p>
                    </div>

                    <div style="text-align: center; color: #666; font-size: 12px;">
                        <p>&copy; 2024 {{appName}}. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """)
                                                            .requiredParams(List.of("userName", "loginTime", "appName"))
                                                            .defaultParams(Map.of(
                                                                    "supportEmail", "support@ftcshop.com",
                                                                    "appName", "FTCSHOP"
                                                            ))
                                                            .description("Login success notification email")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created LOGIN_SUCCESS template");
    }
    private void createOtpTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("OTP_VERIFICATION").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("OTP_VERIFICATION")
                                                            .channel("EMAIL")
                                                            .subject("Mã xác thực OTP của bạn")
                                                            .htmlContent("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Mã OTP</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: #2196F3; padding: 20px; text-align: center; color: white;">
                                <h2>Xác thực tài khoản</h2>
                            </div>
                            
                            <div style="padding: 30px; background: #f9f9f9;">
                                <p>Xin chào <strong>{{userName}}</strong>,</p>
                                <p>Bạn đã yêu cầu mã xác thực để {{purpose}}. Vui lòng sử dụng mã OTP dưới đây:</p>
                                
                                <div style="text-align: center; margin: 30px 0;">
                                    <div style="background: #fff; border: 2px dashed #2196F3; padding: 20px; font-size: 32px; font-weight: bold; color: #2196F3; letter-spacing: 5px;">
                                        {{otpCode}}
                                    </div>
                                </div>
                                
                                <p><strong>Lưu ý:</strong></p>
                                <ul>
                                    <li>Mã OTP này có hiệu lực trong {{expiryMinutes}} phút</li>
                                    <li>Không chia sẻ mã này với bất kỳ ai</li>
                                    <li>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này</li>
                                </ul>
                            </div>
                        </div>
                    </body>
                    </html>
                    """)
                                                            .requiredParams(List.of("userName", "otpCode", "purpose"))
                                                            .defaultParams(Map.of(
                                                                    "expiryMinutes", "5",
                                                                    "purpose", "xác thực tài khoản"
                                                            ))
                                                            .description("OTP verification email template")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created OTP_VERIFICATION template");
    }

    private void createOrderConfirmationTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("ORDER_CONFIRMATION").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("ORDER_CONFIRMATION")
                                                            .channel("EMAIL")
                                                            .subject("Xác nhận đơn hàng #{{orderNumber}}")
                                                            .htmlContent("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Xác nhận đơn hàng</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: #4CAF50; padding: 20px; text-align: center; color: white;">
                                <h2>✅ Đơn hàng đã được xác nhận</h2>
                            </div>
                            
                            <div style="padding: 30px; background: #f9f9f9;">
                                <p>Xin chào <strong>{{customerName}}</strong>,</p>
                                <p>Cảm ơn bạn đã đặt hàng! Đơn hàng của bạn đã được xác nhận và đang được xử lý.</p>
                                
                                <div style="background: white; padding: 20px; margin: 20px 0; border-left: 4px solid #4CAF50;">
                                    <h3>Thông tin đơn hàng</h3>
                                    <p><strong>Số đơn hàng:</strong> #{{orderNumber}}</p>
                                    <p><strong>Ngày đặt:</strong> {{orderDate}}</p>
                                    <p><strong>Tổng tiền:</strong> {{totalAmount}} VND</p>
                                    <p><strong>Phương thức thanh toán:</strong> {{paymentMethod}}</p>
                                </div>
                                
                                <div style="background: white; padding: 20px; margin: 20px 0;">
                                    <h3>Địa chỉ giao hàng</h3>
                                    <p>{{shippingAddress}}</p>
                                </div>
                                
                                <p>Đơn hàng dự kiến sẽ được giao trong <strong>{{estimatedDelivery}}</strong>.</p>
                                
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="{{trackingUrl}}" 
                                       style="background: #2196F3; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                                        Theo dõi đơn hàng
                                    </a>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """)
                                                            .requiredParams(List.of("customerName", "orderNumber", "orderDate", "totalAmount"))
                                                            .defaultParams(Map.of(
                                                                    "paymentMethod", "COD",
                                                                    "estimatedDelivery", "3-5 ngày làm việc",
                                                                    "trackingUrl", "https://myapp.com/tracking"
                                                            ))
                                                            .description("Order confirmation email template")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created ORDER_CONFIRMATION template");
    }

    private void createPasswordResetTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("PASSWORD_RESET").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("PASSWORD_RESET")
                                                            .channel("EMAIL")
                                                            .subject("Yêu cầu đặt lại mật khẩu")
                                                            .htmlContent("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Đặt lại mật khẩu</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: #FF9800; padding: 20px; text-align: center; color: white;">
                                <h2>🔐 Đặt lại mật khẩu</h2>
                            </div>
                            
                            <div style="padding: 30px; background: #f9f9f9;">
                                <p>Xin chào <strong>{{userName}}</strong>,</p>
                                <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>
                                
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="{{resetLink}}" 
                                       style="background: #FF9800; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">
                                        Đặt lại mật khẩu
                                    </a>
                                </div>
                                
                                <p><strong>Lưu ý quan trọng:</strong></p>
                                <ul>
                                    <li>Link này sẽ hết hạn sau {{expiryHours}} giờ</li>
                                    <li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</li>
                                    <li>Không chia sẻ link này với bất kỳ ai</li>
                                </ul>
                                
                                <p>Nếu nút không hoạt động, bạn có thể copy link sau:</p>
                                <p style="background: #fff; padding: 10px; word-break: break-all; border: 1px solid #ddd;">
                                    {{resetLink}}
                                </p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """)
                                                            .requiredParams(List.of("userName", "resetLink"))
                                                            .defaultParams(Map.of("expiryHours", "24"))
                                                            .description("Password reset email template")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created PASSWORD_RESET template");
    }

    private void createPromotionTemplate() {
        if (templateRepository.findByTemplateCodeAndActiveTrue("PROMOTION").isPresent()) {
            return;
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode("PROMOTION")
                                                            .channel("EMAIL")
                                                            .subject("🎉 {{promotionTitle}} - Ưu đãi đặc biệt cho bạn!")
                                                            .htmlContent("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Khuyến mãi đặc biệt</title>
                    </head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); padding: 30px; text-align: center; color: white; border-radius: 10px;">
                                <h1>🎉 {{promotionTitle}}</h1>
                                <p style="font-size: 18px;">Ưu đãi đặc biệt dành riêng cho bạn!</p>
                            </div>
                            
                            <div style="padding: 30px; background: #f9f9f9;">
                                <p>Xin chào <strong>{{customerName}}</strong>,</p>
                                <p>{{promotionDescription}}</p>
                                
                                <div style="background: #fff; border: 2px solid #ff6b6b; padding: 20px; text-align: center; margin: 20px 0; border-radius: 10px;">
                                    <h2 style="color: #ff6b6b; margin: 0;">GIẢM {{discountPercent}}%</h2>
                                    <p style="font-size: 18px; margin: 10px 0;"><strong>Mã giảm giá: {{promoCode}}</strong></p>
                                    <p style="color: #666;">Áp dụng cho đơn hàng từ {{minAmount}} VND</p>
                                </div>
                                
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="{{shopUrl}}" 
                                       style="background: #ff6b6b; color: white; padding: 15px 40px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold; font-size: 16px;">
                                        MUA NGAY
                                    </a>
                                </div>
                                
                                <p><strong>Thời hạn:</strong> Từ {{startDate}} đến {{endDate}}</p>
                                <p><em>*Ưu đãi có thể kết thúc sớm. Áp dụng theo điều kiện và điều khoản.</em></p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """)
                                                            .requiredParams(List.of("customerName", "promotionTitle", "discountPercent", "promoCode"))
                                                            .defaultParams(Map.of(
                                                                    "promotionDescription", "Chúng tôi có ưu đãi đặc biệt dành riêng cho bạn!",
                                                                    "minAmount", "500,000",
                                                                    "shopUrl", "https://myapp.com/shop",
                                                                    "startDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                                                    "endDate", LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                            ))
                                                            .description("Promotion email template")
                                                            .active(true)
                                                            .build();

        templateRepository.save(template);
        log.info("Created PROMOTION template");
    }
}
