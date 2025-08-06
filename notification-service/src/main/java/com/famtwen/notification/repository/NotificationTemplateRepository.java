package com.famtwen.notification.repository;

import com.famtwen.notification.entity.NotificationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 4. Repository Interfaces
@Repository
public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByTemplateCodeAndActiveTrue(String templateCode);
    List<NotificationTemplate> findByChannelAndActiveTrue(String channel);
}