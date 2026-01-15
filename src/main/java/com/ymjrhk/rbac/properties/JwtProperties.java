package com.ymjrhk.rbac.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户jwt令牌相关配置
 */
@Component
@ConfigurationProperties(prefix = "rbac.jwt")
@Data
public class JwtProperties {

    private String secretKey;

    private long ttl;

    /**
     * HTTP Header 名，推荐固定为 Authorization
     */
    private String header = "Authorization";

    /**
     * token 前缀
     */
    private String prefix = "Bearer ";
}
