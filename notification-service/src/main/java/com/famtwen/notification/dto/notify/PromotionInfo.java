package com.famtwen.notification.dto.notify;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionInfo {
    String promotionId;
    String title;
    String description;
    Integer discountPercent;
    String promoCode;
    BigDecimal minAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
}