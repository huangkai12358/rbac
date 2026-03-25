package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "用户详情返回参数")
public class UserDetailVO {
    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private String secretToken;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLoginTime;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailVO that = (UserDetailVO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(status, that.status) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime) && Objects.equals(lastLoginTime, that.lastLoginTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, nickname, email, status, version, secretToken, createTime, updateTime, lastLoginTime);
    }

    @Override
    public String toString() {
        return "UserDetailVO" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "status=" + status + ", " + "version=" + version + ", " + "secretToken=" + secretToken + ", " + "createTime=" + createTime + ", " + "updateTime=" + updateTime + ", " + "lastLoginTime=" + lastLoginTime + "}";
    }

}
