package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "审计日志查询返回参数")
/* 不需要userId和permissionId，是给人看的 */
public class AuditLogVO {
    private Long logSeq;

    private String username;

    private String permissionName;

    private String path;

    private String method;

    private String requestBody;

    private String ip;

    private Integer success;

    private String errorMessage;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // 已经扩展了全局消息转换器，所以不需要这个注解了
    private LocalDateTime createTime;
    public Long getLogSeq() {
        return logSeq;
    }

    public void setLogSeq(Long logSeq) {
        this.logSeq = logSeq;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        AuditLogVO that = (AuditLogVO) o;
        return Objects.equals(logSeq, that.logSeq) && Objects.equals(username, that.username) && Objects.equals(permissionName, that.permissionName) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(requestBody, that.requestBody) && Objects.equals(ip, that.ip) && Objects.equals(success, that.success) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logSeq, username, permissionName, path, method, requestBody, ip, success, errorMessage, createTime);
    }

    @Override
    public String toString() {
        return "AuditLogVO" + "{" + "logSeq=" + logSeq + ", " + "username=" + username + ", " + "permissionName=" + permissionName + ", " + "path=" + path + ", " + "method=" + method + ", " + "requestBody=" + requestBody + ", " + "ip=" + ip + ", " + "success=" + success + ", " + "errorMessage=" + errorMessage + ", " + "createTime=" + createTime + "}";
    }

}
