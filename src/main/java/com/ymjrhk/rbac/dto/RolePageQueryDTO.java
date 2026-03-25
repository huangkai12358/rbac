package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;

import java.io.Serializable;
import java.util.Objects;

public class RolePageQueryDTO extends PageQuery implements Serializable {

    // 角色名
    private String roleName;

    // 排序字段
    private String sortField;

    // 排序方式：asc / desc
    private String sortOrder;

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
        RolePageQueryDTO that = (RolePageQueryDTO) o;
        return Objects.equals(getPageNum(), that.getPageNum())
                && Objects.equals(getPageSize(), that.getPageSize())
                && Objects.equals(roleName, that.roleName)
                && Objects.equals(sortField, that.sortField)
                && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(orderBy, that.orderBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), roleName, sortField, sortOrder, orderBy);
    }

    @Override
    public String toString() {
        return "RolePageQueryDTO" + "{" + "roleName=" + roleName + ", " + "sortField=" + sortField + ", " + "sortOrder=" + sortOrder + ", " + "orderBy=" + orderBy + "}";
    }

}
