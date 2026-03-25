package com.ymjrhk.rbac.dto;
import java.util.Objects;


// 修改用
// 和 RoleCreateDTO 相比少 status
public class RoleDTO {
    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer version;

    private String secretToken;

//    private Integer status;
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
        RoleDTO that = (RoleDTO) o;
        return Objects.equals(roleName, that.roleName) && Objects.equals(roleDisplayName, that.roleDisplayName) && Objects.equals(description, that.description) && Objects.equals(version, that.version) && Objects.equals(secretToken, that.secretToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName, roleDisplayName, description, version, secretToken);
    }

    @Override
    public String toString() {
        return "RoleDTO" + "{" + "roleName=" + roleName + ", " + "roleDisplayName=" + roleDisplayName + ", " + "description=" + description + ", " + "version=" + version + ", " + "secretToken=" + secretToken + "}";
    }

}
