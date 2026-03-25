package com.ymjrhk.rbac.dto;
import java.util.Objects;


// 修改用
// 和 PermissionCreateDTO 相比少 status
public class PermissionDTO {
    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;

    private Integer version;

    private String secretToken;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionDTO that = (PermissionDTO) o;
        return Objects.equals(permissionName, that.permissionName) && Objects.equals(permissionDisplayName, that.permissionDisplayName) && Objects.equals(description, that.description) && Objects.equals(type, that.type) && Objects.equals(parentId, that.parentId) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(sort, that.sort) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionName, permissionDisplayName, description, type, parentId, path, method, sort, version, secretToken);
    }

    @Override
    public String toString() {
        return "PermissionDTO" + "{" + "permissionName=" + permissionName + ", " + "permissionDisplayName=" + permissionDisplayName + ", " + "description=" + description + ", " + "type=" + type + ", " + "parentId=" + parentId + ", " + "path=" + path + ", " + "method=" + method + ", " + "sort=" + sort + ", " + "version=" + version + ", " + "secretToken=" + secretToken + "}";
    }

}
