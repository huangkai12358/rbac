package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PermissionHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long permissionId;

    private Integer version;

    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer status;

    private String secretToken;

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;

    private Integer operateType;

    private LocalDateTime operateTime;

    private Long operatorId;
}
