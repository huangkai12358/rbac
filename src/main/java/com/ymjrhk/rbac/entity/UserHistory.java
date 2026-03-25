package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer version;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private String secretToken;

    private Integer authVersion;

    private Integer operateType;

    private LocalDateTime operateTime;

    private Long operatorId;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public LocalDateTime getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(LocalDateTime operateTime) {
        this.operateTime = operateTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserHistory that = (UserHistory) o;
        return Objects.equals(userId, that.userId) && Objects.equals(version, that.version) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(status, that.status) && Objects.equals(secretToken, that.secretToken) && Objects.equals(authVersion, that.authVersion) && Objects.equals(operateType, that.operateType) && Objects.equals(operateTime, that.operateTime) && Objects.equals(operatorId, that.operatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, version, username, password, nickname, email, status, secretToken, authVersion, operateType, operateTime, operatorId);
    }

    @Override
    public String toString() {
        return "UserHistory" + "{" + "userId=" + userId + ", " + "version=" + version + ", " + "username=" + username + ", " + "password=" + password + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "status=" + status + ", " + "secretToken=" + secretToken + ", " + "authVersion=" + authVersion + ", " + "operateType=" + operateType + ", " + "operateTime=" + operateTime + ", " + "operatorId=" + operatorId + "}";
    }

}
