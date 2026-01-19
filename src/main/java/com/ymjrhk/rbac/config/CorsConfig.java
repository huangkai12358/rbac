package com.ymjrhk.rbac.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 允许跨域访问的路径
                .allowedOrigins("http://localhost:5173") // 允许跨域访问的源
                .allowedMethods("*") // 允许请求方法
                .maxAge(16800) // 预检间隔时间
                .allowedHeaders("*") // 允许头部设置
                .allowCredentials(true); // 是否发送 cookie
    }
}

