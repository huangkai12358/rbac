package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "个人权限查询返回参数（不包括禁用）")
public class MePermissionVO {

    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;
    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionDisplayName() {
        return permissionDisplayName;
    }

    public void setPermissionDisplayName(String permissionDisplayName) {
        this.permissionDisplayName = permissionDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MePermissionVO that = (MePermissionVO) o;
        return Objects.equals(permissionId, that.permissionId) && Objects.equals(permissionName, that.permissionName) && Objects.equals(permissionDisplayName, that.permissionDisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permissionName, permissionDisplayName);
    }

    @Override
    public String toString() {
        return "MePermissionVO" + "{" + "permissionId=" + permissionId + ", " + "permissionName=" + permissionName + ", " + "permissionDisplayName=" + permissionDisplayName + "}";
    }

}
