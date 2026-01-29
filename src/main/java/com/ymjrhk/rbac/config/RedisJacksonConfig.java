package com.ymjrhk.rbac.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.ymjrhk.rbac.json.JacksonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis ObjectMapper（带 typing 类型推断）
 * 在 new JacksonObjectMapper() 基础上 activateDefaultTyping
 */
@Configuration
public class RedisJacksonConfig {

    @Bean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new JacksonObjectMapper();

        /*
         * 开启类型信息（解决 Redis / Cache 反序列化成 LinkedHashMap 的问题，LinkedHashMap cannot be cast to UserVO）
         *
         * JSON 中会自动加入：
         * {
         *   "@class": "com.xxx.UserVO",
         *   ...
         * }
         */
        // 只在 Redis 打开类型信息
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }
}

