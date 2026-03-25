package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Schema(description = "个人信息查询返回参数，不含status，被禁用的用户不能登录")
public class MeViewVO {
    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private LocalDateTime createTime;

    @Schema(description = "当前用户角色列表")
    private List<MeRoleVO> roles;

    @Schema(description = "当前用户权限列表")
    private List<MePermissionVO> permissions;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<MeRoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<MeRoleVO> roles) {
        this.roles = roles;
    }

    public List<MePermissionVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<MePermissionVO> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeViewVO that = (MeViewVO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(createTime, that.createTime) && Objects.equals(roles, that.roles) && Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, nickname, email, createTime, roles, permissions);
    }

    @Override
    public String toString() {
        return "MeViewVO" + "{" + "userId=" + userId + ", " + "username=" + username + ", " + "nickname=" + nickname + ", " + "email=" + email + ", " + "createTime=" + createTime + ", " + "roles=" + roles + ", " + "permissions=" + permissions + "}";
    }

}
