package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "权限导出参数")
public class PermissionExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long permissionId;

    @ExcelProperty(value = "权限标识", index = 1)
    @ColumnWidth(30)
    private String permissionName;

    @ExcelProperty(value = "权限名称", index = 2)
    @ColumnWidth(20)
    private String permissionDisplayName;

    @ExcelProperty(value = "类型", index = 3)
    @ColumnWidth(10)
    private Integer type;

    @ExcelProperty(value = "路径", index = 4)
    @ColumnWidth(30)
    private String path;

    @ExcelProperty(value = "方法", index = 5)
    @ColumnWidth(10)
    private String method;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 7)
    @ColumnWidth(25)
    private LocalDateTime createTime;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionExcelVO that = (PermissionExcelVO) o;
        return Objects.equals(permissionId, that.permissionId) && Objects.equals(permissionName, that.permissionName) && Objects.equals(permissionDisplayName, that.permissionDisplayName) && Objects.equals(type, that.type) && Objects.equals(path, that.path) && Objects.equals(method, that.method) && Objects.equals(status, that.status) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionId, permissionName, permissionDisplayName, type, path, method, status, createTime);
    }

    @Override
    public String toString() {
        return "PermissionExcelVO" + "{" + "permissionId=" + permissionId + ", " + "permissionName=" + permissionName + ", " + "permissionDisplayName=" + permissionDisplayName + ", " + "type=" + type + ", " + "path=" + path + ", " + "method=" + method + ", " + "status=" + status + ", " + "createTime=" + createTime + "}";
    }

}
