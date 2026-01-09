package com.ymjrhk.rbac.dto;

import lombok.Data;

// 修改用
// 和 PermissionCreateDTO 相比少 status
@Data
public class PermissionDTO {
    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;
}
