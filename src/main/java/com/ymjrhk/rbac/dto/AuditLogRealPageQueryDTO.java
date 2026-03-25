package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实际的审计日志分页查询DTO，把日期转化成了具体时间
 */
public class AuditLogRealPageQueryDTO extends PageQuery implements Serializable {

    private Long logSeq;

    private String username;

    private String permissionName;

    private Integer success;

    // 开始时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime startTime;
    // 结束时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime endTime;

    // 前端传：排序字段、排序方向
    private String sortField; // e.g. "createTime", "username", "ip"

    private String sortOrder; // "asc" / "desc"

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
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

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuditLogRealPageQueryDTO that = (AuditLogRealPageQueryDTO) o;
        return Objects.equals(getPageNum(), that.getPageNum())
                && Objects.equals(getPageSize(), that.getPageSize())
                && Objects.equals(logSeq, that.logSeq)
                && Objects.equals(username, that.username)
                && Objects.equals(permissionName, that.permissionName)
                && Objects.equals(success, that.success)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(sortField, that.sortField)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(orderBy, that.orderBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), logSeq, username, permissionName, success, startTime, endTime, sortField, sortOrder, orderBy);
    }

    @Override
    public String toString() {
        return "AuditLogRealPageQueryDTO" + "{" + "logSeq=" + logSeq + ", " + "username=" + username + ", " + "permissionName=" + permissionName + ", " + "success=" + success + ", " + "startTime=" + startTime + ", " + "endTime=" + endTime + ", " + "sortField=" + sortField + ", " + "sortOrder=" + sortOrder + ", " + "orderBy=" + orderBy + "}";
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

}
