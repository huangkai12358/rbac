package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限查询返回参数")
public class PermissionVO {
    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer status;

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;

    // TODO: private Boolean assigned; 该角色是否已拥有 可能要加？前端勾选框直接用
}
