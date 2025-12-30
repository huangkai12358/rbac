package com.ymjrhk.rbac.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rbac.jwt")
@Data
public class JwtProperties {

    /**
     * 用户jwt令牌相关配置
     */
    private String secretKey;
    private long ttl;
    private String tokenName;
}
