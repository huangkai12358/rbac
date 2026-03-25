package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "角色导出参数")
public class RoleExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long roleId;

    @ExcelProperty(value = "角色标识", index = 1)
    @ColumnWidth(20)
    private String roleName;

    @ExcelProperty(value = "角色名称", index = 2)
    @ColumnWidth(20)
    private String roleDisplayName;

    @ExcelProperty(value = "描述", index = 3)
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private LocalDateTime createTime;
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
        RoleExcelVO that = (RoleExcelVO) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName, roleDisplayName, description, status, createTime);
    }

    @Override
    public String toString() {
        return "RoleExcelVO" + "{" + "roleId=" + roleId + ", " + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "status=" + status + ", " + "createTime=" + createTime + "}";
    }

}
