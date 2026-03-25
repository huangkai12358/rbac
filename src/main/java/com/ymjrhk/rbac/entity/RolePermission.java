package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long permissionId;

    private LocalDateTime createTime;

    private Long createUserId;
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RolePermission that = (RolePermission) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(permissionId, that.permissionId) && Objects.equals(createTime, that.createTime) && Objects.equals(createUserId, that.createUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId, createTime, createUserId);
    }

    @Override
    public String toString() {
        return "RolePermission" + "{" + "roleId=" + roleId + ", " + "permissionId=" + permissionId + ", " + "createTime=" + createTime + ", " + "createUserId=" + createUserId + "}";
    }

}
