package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
}
