package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class User implements Serializable, OptimisticLockEntity {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private Integer authVersion;

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(Integer authVersion) {
        this.authVersion = authVersion;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User that = (User) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(status, that.status) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken) && Objects.equals(authVersion, that.authVersion) && Objects.equals(createTime, that.createTime) && Objects.equals(createUserId, that.createUserId) && Objects.equals(updateTime, that.updateTime) && Objects.equals(updateUserId, that.updateUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, nickname, email, status, version, secretToken, authVersion, createTime, createUserId, updateTime, updateUserId);
    }

    @Override
    public String toString() {
        return "User" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "password=" + password + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "status=" + status + ", " + "version=" + version + ", " + "secretToken=" + secretToken + ", " + "authVersion=" + authVersion + ", " + "createTime=" + createTime + ", " + "createUserId=" + createUserId + ", " + "updateTime=" + updateTime + ", " + "updateUserId=" + updateUserId + "}";
    }

    public String getNewSecretToken() {
        return newSecretToken;
    }

    public void setNewSecretToken(String newSecretToken) {
        this.newSecretToken = newSecretToken;
    }

}