package com.ymjrhk.rbac.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.ymjrhk.rbac.constant.CacheConstant.*;

/**
 * Spring Cache 专用
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private final ObjectMapper redisObjectMapper;

    // 使用自己写构造器注入
    // 涉及 @Qualifier，无法使用 Lombok 构造器 @RequiredArgsConstructor（无法拷贝注解）
    public CacheConfig(
            @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper
    ) {
        this.redisObjectMapper = redisObjectMapper;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        // JSON 序列化
        RedisSerializer<Object> jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // 默认配置
        RedisCacheConfiguration baseConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                                       .serializeValuesWith(
                                               RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                                       )
                                       .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

        // 用户基本信息
        configMap.put(USER_BASIC, baseConfig.entryTtl(Duration.ofMinutes(30)));

        // 用户权限
        configMap.put(USER_PERMISSIONS, baseConfig.entryTtl(Duration.ofMinutes(5)));

        // 登录鉴权信息
        configMap.put(USER_AUTH, baseConfig.entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(factory)
                                .cacheDefaults(baseConfig.entryTtl(Duration.ofMinutes(5))) // 默认 TTL
                                .withInitialCacheConfigurations(configMap)
                                .build();

    }
}
