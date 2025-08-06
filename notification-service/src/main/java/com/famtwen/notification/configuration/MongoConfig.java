package com.famtwen.notification.configuration;


import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

// 12. MongoDB Configuration
@Configuration
@EnableMongoRepositories(basePackages = "com.famtwen.notification.repository")
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "notification_db");
    }
}