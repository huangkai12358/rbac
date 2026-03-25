package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AuditLogPageQueryDTO extends PageQuery implements Serializable {

    @Min(value = 1, message = "序号必须为正整数")
    private Long logSeq;

    private String username;

    private String permissionName;

    private Integer success;

    // 只接收日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
        AuditLogPageQueryDTO that = (AuditLogPageQueryDTO) o;
        return Objects.equals(getPageNum(), that.getPageNum())
                && Objects.equals(getPageSize(), that.getPageSize())
                && Objects.equals(logSeq, that.logSeq)
                && Objects.equals(username, that.username)
                && Objects.equals(permissionName, that.permissionName)
                && Objects.equals(success, that.success)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(sortField, that.sortField)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(orderBy, that.orderBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), logSeq, username, permissionName, success, startDate, endDate, sortField, sortOrder, orderBy);
    }

    @Override
    public String toString() {
        return "AuditLogPageQueryDTO" + "{" + "logSeq=" + logSeq + ", " + "username=" + username + ", " + "permissionName=" + permissionName + ", " + "success=" + success + ", " + "startDate=" + startDate + ", " + "endDate=" + endDate + ", " + "sortField=" + sortField + ", " + "sortOrder=" + sortOrder + ", " + "orderBy=" + orderBy + "}";
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
