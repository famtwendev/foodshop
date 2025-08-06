package com.famtwen.identity_service.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


@Configuration
@EnableCaching // Thêm annotation này
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());     // key là String
        template.setValueSerializer(new StringRedisSerializer());   // value là String
        return template;
    }

    // Thêm CacheManager bean vào RedisConfig hiện tại
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                                                .entryTtl(Duration.ofSeconds(30)) // Default TTL cho introspection cache
                                                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                                        .fromSerializer(new StringRedisSerializer()))
                                                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(config)
                                .build();
    }
}
