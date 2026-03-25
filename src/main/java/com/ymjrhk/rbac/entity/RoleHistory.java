package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class RoleHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Integer version;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;

    private String secretToken;

    private Integer operateType;

    private LocalDateTime operateTime;

    private Long operatorId;
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public LocalDateTime getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(LocalDateTime operateTime) {
        this.operateTime = operateTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleHistory that = (RoleHistory) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(version, that.version) && Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(secretToken, that.secretToken) && Objects.equals(operateType, that.operateType) && Objects.equals(operateTime, that.operateTime) && Objects.equals(operatorId, that.operatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, version, roleName, roleDisplayName, description, status, secretToken, operateType, operateTime, operatorId);
    }

    @Override
    public String toString() {
        return "RoleHistory" + "{" + "roleId=" + roleId + ", " + "version=" + version + ", " + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "secretToken=" + secretToken + ", " + "operateType=" + operateType + ", " + "operateTime=" + operateTime + ", " + "operatorId=" + operatorId + "}";
    }

}
