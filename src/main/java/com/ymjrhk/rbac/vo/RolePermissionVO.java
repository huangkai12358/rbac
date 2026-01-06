package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
public class RolePermissionVO {
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
}
