package com.famtwen.notification.repository;

import com.famtwen.notification.entity.NotificationHistory;
import com.famtwen.notification.exception.NotificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationHistoryRepository extends MongoRepository<NotificationHistory, String> {
    List<NotificationHistory> findByRecipientOrderByCreatedAtDesc(String recipient);
    List<NotificationHistory> findByTemplateCodeAndStatus(String templateCode, NotificationStatus status);
    List<NotificationHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
