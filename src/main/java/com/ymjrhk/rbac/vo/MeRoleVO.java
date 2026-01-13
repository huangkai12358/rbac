package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人角色查询返回参数（不包括禁用）")
public class MeRoleVO {

    private Long roleId;

    private String roleName;

    private String roleDisplayName;
}
