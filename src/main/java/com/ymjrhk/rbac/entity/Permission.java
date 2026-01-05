package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Permission implements Serializable, OptimisticLockEntity{
    private static final long serialVersionUID = 1L;

    private Long permissionId;

    private String permissionName;

    private String permissionDisplayName;

    private String description;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private Integer type;

    private Long parentId;

    private String path;

    private String method;

    private Integer sort;

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
}
