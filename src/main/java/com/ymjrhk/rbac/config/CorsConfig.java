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
                .exposedHeaders("Content-Disposition") // 由于 CORS 限制，浏览器默认不允许前端脚本读取 Content-Disposition 响应头，需要通过 Access-Control-Expose-Headers 显式暴露。
                .allowCredentials(true); // 是否发送 cookie

    }
}
