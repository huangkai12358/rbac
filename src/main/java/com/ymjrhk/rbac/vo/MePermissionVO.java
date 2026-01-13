package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人权限查询返回参数（不包括禁用）")
public class MePermissionVO {

    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;
}
