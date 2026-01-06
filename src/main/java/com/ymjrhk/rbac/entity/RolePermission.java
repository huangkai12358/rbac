package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long permissionId;

    private LocalDateTime createTime;

    private Long createUserId;
}
