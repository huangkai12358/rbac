package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Role implements Serializable, OptimisticLockEntity {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDisplayName() {
        return roleDisplayName;
    }

    public void setRoleDisplayName(String roleDisplayName) {
        this.roleDisplayName = roleDisplayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role that = (Role) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken) && Objects.equals(createTime, that.createTime) && Objects.equals(createUserId, that.createUserId) && Objects.equals(updateTime, that.updateTime) && Objects.equals(updateUserId, that.updateUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDisplayName, description, status, version, secretToken, createTime, createUserId, updateTime, updateUserId);
    }

    @Override
    public String toString() {
        return "Role" + "{" + "roleId=" + roleId + ", " + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "version=" + version + ", " + "secretToken=" + secretToken + ", " + "createTime=" + createTime + ", " + "createUserId=" + createUserId + ", " + "updateTime=" + updateTime + ", " + "updateUserId=" + updateUserId + "}";
    }

    public String getNewSecretToken() {
        return newSecretToken;
    }

    public void setNewSecretToken(String newSecretToken) {
        this.newSecretToken = newSecretToken;
    }

}