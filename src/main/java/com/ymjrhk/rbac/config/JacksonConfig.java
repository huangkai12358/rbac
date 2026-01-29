package com.ymjrhk.rbac.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymjrhk.rbac.json.JacksonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP / 默认的 ObjectMapper
 */
@Configuration
public class JacksonConfig {

    @Bean
//    @Primary // Spring 里所有需要 ObjectMapper 的地方，优先用这个。HTTP / Controller / MVC 默认用它
    public ObjectMapper objectMapper() {
        return new JacksonObjectMapper();
    }
}
