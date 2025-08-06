package com.famtwen.notification.services;

import com.famtwen.notification.event.NotificationEvent;
import com.famtwen.notification.dto.notify.BatchResult;
import com.famtwen.notification.dto.notify.PromotionInfo;
import com.famtwen.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchNotificationService {
    private final EmailService emailService;
    private final NotificationHistoryRepository historyRepository;

    @Async
    public CompletableFuture<BatchResult> sendBulkNotifications(List<NotificationEvent> events) {
        BatchResult result = new BatchResult();

        for (NotificationEvent event : events) {
            try {
                emailService.sendTemplatedEmail(event);
                result.incrementSuccess();
                log.info("Sent notification to: {}", event.getRecipient());
            } catch (Exception e) {
                result.incrementFailed();
                log.error("Failed to send notification to: {}, error: {}", event.getRecipient(), e.getMessage());
            }
        }

        return CompletableFuture.completedFuture(result);
    }

    public void sendPromotionToAllUsers(List<String> userEmails, PromotionInfo promotion) {
        List<NotificationEvent> events = userEmails.stream()
                                                   .map(email -> NotificationEvent.builder()
                                                                                  .channel("EMAIL")
                                                                                  .recipient(email)
                                                                                  .templateCode("PROMOTION")
                                                                                  .params(Map.of(
                                                                                          "customerName", "Khách hàng thân thiết",
                                                                                          "promotionTitle", promotion.getTitle(),
                                                                                          "promotionDescription", promotion.getDescription(),
                                                                                          "discountPercent", promotion.getDiscountPercent().toString(),
                                                                                          "promoCode", promotion.getPromoCode(),
                                                                                          "minAmount", NumberFormat.getInstance(new Locale("vi", "VN")).format(promotion.getMinAmount()),
                                                                                          "startDate", promotion.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                                                                          "endDate", promotion.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                                                  ))
                                                                                  .correlationId(promotion.getPromotionId())
                                                                                  .build())
                                                   .collect(Collectors.toList());

        sendBulkNotifications(events);
    }
}