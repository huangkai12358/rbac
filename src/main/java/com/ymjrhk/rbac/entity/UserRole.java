package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long roleId;

    private LocalDateTime createTime;

    private Long createUserId;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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
        UserRole that = (UserRole) o;
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId) && Objects.equals(createTime, that.createTime) && Objects.equals(createUserId, that.createUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId, createTime, createUserId);
    }

    @Override
    public String toString() {
        return "UserRole" + "{" + "userId=" + userId + ", " + "roleId=" + roleId + ", " + "createTime=" + createTime + ", " + "createUserId=" + createUserId + "}";
    }

}
