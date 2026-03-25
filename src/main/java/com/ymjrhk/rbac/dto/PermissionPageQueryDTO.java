package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;

import java.io.Serializable;
import java.util.Objects;

public class PermissionPageQueryDTO extends PageQuery implements Serializable {

    // 权限名
    private String permissionName;

    // 排序字段
    private String sortField;

    // 排序方式：asc / desc
    private String sortOrder;

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
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
        PermissionPageQueryDTO that = (PermissionPageQueryDTO) o;
        return Objects.equals(getPageNum(), that.getPageNum())
                && Objects.equals(getPageSize(), that.getPageSize())
                && Objects.equals(permissionName, that.permissionName)
                && Objects.equals(sortField, that.sortField)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(orderBy, that.orderBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), permissionName, sortField, sortOrder, orderBy);
    }

    @Override
    public String toString() {
        return "PermissionPageQueryDTO" + "{" + "permissionName=" + permissionName + ", " + "sortField=" + sortField + ", " + "sortOrder=" + sortOrder + ", " + "orderBy=" + orderBy + "}";
    }

}
