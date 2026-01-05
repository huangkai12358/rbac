package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;
}
