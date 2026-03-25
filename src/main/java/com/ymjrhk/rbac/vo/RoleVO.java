package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "角色查询返回参数")
public class RoleVO {
    private Long roleId;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;

    private Integer version;

    private String secretToken;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleVO that = (RoleVO) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDisplayName, description, status, version, secretToken, createTime, updateTime);
    }

    @Override
    public String toString() {
        return "RoleVO" + "{" + "roleId=" + roleId + ", " + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "version=" + version + ", " + "secretToken=" + secretToken + ", " + "createTime=" + createTime + ", " + "updateTime=" + updateTime + "}";
    }

}
