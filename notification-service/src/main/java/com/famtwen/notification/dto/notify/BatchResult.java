package com.famtwen.notification.dto.notify;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchResult {
    private int successCount = 0;
    private int failedCount = 0;

    public void incrementSuccess() {
        successCount++;
    }

    public void incrementFailed() {
        failedCount++;
    }

    public int getTotalCount() {
        return successCount + failedCount;
    }
}