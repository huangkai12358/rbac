package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long logSeq;

    private Long userId;

    private String username;

    private String permissionId;

    private String permissionName;

    private String path;

    private String method;

    private String requestBody;

    private String ip;

    private Integer success;

    private String errorMessage;

    private LocalDateTime createTime;
    public Long getLogSeq() {
        return logSeq;
    }

    public void setLogSeq(Long logSeq) {
        this.logSeq = logSeq;
    }

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

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuditLog that = (AuditLog) o;
        return Objects.equals(logSeq, that.logSeq) && Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(permissionId, that.permissionId) && Objects.equals(permissionName, that.permissionName) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(requestBody, that.requestBody) && Objects.equals(ip, that.ip) && Objects.equals(success, that.success) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logSeq, userId, username, permissionId, permissionName, path, method, requestBody, ip, success, errorMessage, createTime);
    }

    @Override
    public String toString() {
        return "AuditLog" + "{" + "logSeq=" + logSeq + ", " + "userId=" + userId + ", " + "username=" + username + ", " + "permissionId=" + permissionId + ", " + "permissionName=" + permissionName + ", " + "path=" + path + ", " + "method=" + method + ", " + "requestBody=" + requestBody + ", " + "ip=" + ip + ", " + "success=" + success + ", " + "errorMessage=" + errorMessage + ", " + "createTime=" + createTime + "}";
    }

}
