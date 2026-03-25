package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "审计日志导出参数")
public class AuditLogExcelVO {

    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(10)
    private Long logSeq;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "权限名", index = 2)
    @ColumnWidth(30)
    private String permissionName;

    @ExcelProperty(value = "路径", index = 3)
    @ColumnWidth(30)
    private String path;

    @ExcelProperty(value = "方法", index = 4)
    @ColumnWidth(10)
    private String method;

    @ExcelProperty(value = "请求体", index = 5)
    @ColumnWidth(70)
    private String requestBody;

    @ExcelProperty(value = "IP 地址", index = 6)
    @ColumnWidth(20)
    private String ip;

    @ExcelProperty(value = "是否成功", index = 7)
    @ColumnWidth(20)
    private String success; // 注意此处 success 为 String

    @ExcelProperty(value = "错误信息", index = 8)
    @ColumnWidth(20)
    private String errorMessage;

    @ExcelProperty(value = "操作时间", index = 9)
    @ColumnWidth(25)
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
        AuditLogExcelVO that = (AuditLogExcelVO) o;
        return Objects.equals(logSeq, that.logSeq) && Objects.equals(username, that.username) && Objects.equals(permissionName, that.permissionName) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(requestBody, that.requestBody) && Objects.equals(ip, that.ip) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logSeq, username, permissionName, path, method, requestBody, ip, errorMessage, createTime);
    }

    @Override
    public String toString() {
        return "AuditLogExcelVO" + "{" + "logSeq=" + logSeq + ", " + "username=" + username + ", " + "permissionName=" + permissionName + ", " + "path=" + path + ", " + "method=" + method + ", " + "requestBody=" + requestBody + ", " + "ip=" + ip + ", " + "errorMessage=" + errorMessage + ", " + "createTime=" + createTime + "}";
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

}