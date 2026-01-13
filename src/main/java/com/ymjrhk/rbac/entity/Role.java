package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Role implements Serializable, OptimisticLockEntity {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
}
