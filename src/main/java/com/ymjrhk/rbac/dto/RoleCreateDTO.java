package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Schema(description = "角色创建参数")
// 和 RoleCreateDTO 相比少 id，多 status
public class RoleCreateDTO {
    @Schema(description = "角色名", example = "USER")
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    @Schema(description = "角色显示名称", example = "普通用户")
    @NotBlank(message = "角色显示名称不能为空")
    private String roleDisplayName;

    @Schema(description = "描述", example = "这是一个普通用户")
    private String description;

    @Schema(description = "状态", example = "1")
    private Integer status;
    // TODO：要 status 吗
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleCreateDTO that = (RoleCreateDTO) o;
        return Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName, roleDisplayName, description, status);
    }

    @Override
    public String toString() {
        return "RoleCreateDTO" + "{" + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "status=" + status + "}";
    }

}
