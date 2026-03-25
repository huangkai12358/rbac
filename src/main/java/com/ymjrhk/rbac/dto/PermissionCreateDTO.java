package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Schema(description = "权限创建参数")
public class PermissionCreateDTO {
    @Schema(description = "权限名", example = "ROLE:VIEW")
    @NotBlank(message = "权限名不能为空")
    private String permissionName;

    @Schema(description = "权限显示名称", example = "查看角色")
    @NotBlank(message = "权限显示名称不能为空")
    private String permissionDisplayName;

    @Schema(description = "描述", example = "角色分页/详情/权限查询")
    private String description;

    @Schema(description = "状态", example = "1")
    private Integer status;
    // TODO：要 status 吗

    @Schema(description = "类型", example = "2")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "父权限ID", example = "3")
    @NotNull(message = "父权限ID不能为空")
    private Long parentId;

    @Schema(description = "请求路径", example = "/api/roles/**")
    private String path;

    @Schema(description = "方法", example = "GET")
    private String method;

    @Schema(description = "排序", example = "1")
    private Integer sort;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionCreateDTO that = (PermissionCreateDTO) o;
        return Objects.equals(permissionName, that.permissionName) && Objects.equals(permissionDisplayName, that.permissionDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(type, that.type) && Objects.equals(parentId, that.parentId) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionName, permissionDisplayName, description, status, type, parentId, path, method, sort);
    }

    @Override
    public String toString() {
        return "PermissionCreateDTO" + "{" + "permissionName=" + permissionName + ", " + "permissionDisplayName=" + permissionDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "type=" + type + ", " + "parentId=" + parentId + ", " + "path=" + path + ", " + "method=" + method + ", " + "sort=" + sort + "}";
    }

}
