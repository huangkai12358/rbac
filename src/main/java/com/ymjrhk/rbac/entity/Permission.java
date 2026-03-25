package com.ymjrhk.rbac.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Permission implements Serializable, OptimisticLockEntity {
    private static final long serialVersionUID = 1L;

    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
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
        Permission that = (Permission) o;
        return Objects.equals(permissionId, that.permissionId) && Objects.equals(permissionName, that.permissionName) && Objects.equals(permissionDisplayName, that.permissionDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken) && Objects.equals(type, that.type) && Objects.equals(parentId, that.parentId) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(sort, that.sort) && Objects.equals(createTime, that.createTime) && Objects.equals(createUserId, that.createUserId) && Objects.equals(updateTime, that.updateTime) && Objects.equals(updateUserId, that.updateUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permissionName, permissionDisplayName, description, status, version, secretToken, type, parentId, path, method, sort, createTime, createUserId, updateTime, updateUserId);
    }

    @Override
    public String toString() {
        return "Permission" + "{" + "permissionId=" + permissionId + ", " + "permissionName=" + permissionName + ", " + "permissionDisplayName=" + permissionDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "version=" + version + ", " + "secretToken=" + secretToken + ", " + "type=" + type + ", " + "parentId=" + parentId + ", " + "path=" + path + ", " + "method=" + method + ", " + "sort=" + sort + ", " + "createTime=" + createTime + ", " + "createUserId=" + createUserId + ", " + "updateTime=" + updateTime + ", " + "updateUserId=" + updateUserId + "}";
    }

    public String getNewSecretToken() {
        return newSecretToken;
    }

    public void setNewSecretToken(String newSecretToken) {
        this.newSecretToken = newSecretToken;
    }

}