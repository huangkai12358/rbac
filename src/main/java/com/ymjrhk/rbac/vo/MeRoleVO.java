package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "个人角色查询返回参数（不包括禁用）")
public class MeRoleVO {

    private Long roleId;

    private String roleName;

    private String roleDisplayName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeRoleVO that = (MeRoleVO) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDisplayName);
    }

    @Override
    public String toString() {
        return "MeRoleVO" + "{" + "roleId=" + roleId + ", " + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + "}";
    }

}
