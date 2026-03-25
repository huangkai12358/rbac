package com.ymjrhk.rbac.dto.auth;
import java.util.Objects;


/**
 * 数据库中用户登录所需的验证信息
 * （数据库里现在是谁）
 */
public class UserAuthInfo {
    private Long userId;
    private String username;
    private Integer status;
    private Integer authVersion;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(Integer authVersion) {
        this.authVersion = authVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAuthInfo that = (UserAuthInfo) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(status, that.status) && Objects.equals(authVersion, that.authVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, status, authVersion);
    }

    @Override
    public String toString() {
        return "UserAuthInfo" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "status=" + status + ", " + "authVersion=" + authVersion + "}";
    }

}
