package com.ymjrhk.rbac.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * 用户jwt令牌相关配置
 */
@Component
@ConfigurationProperties(prefix = "rbac.jwt")
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
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JwtProperties that = (JwtProperties) o;
        return Objects.equals(secretKey, that.secretKey) && Objects.equals(ttl, that.ttl) && Objects.equals(header, that.header) && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secretKey, ttl, header, prefix);
    }

    @Override
    public String toString() {
        return "JwtProperties" + "{" + "secretKey=" + secretKey + ", " + "ttl=" + ttl + ", " + "header=" + header + ", " + "prefix=" + prefix + "}";
    }

}
