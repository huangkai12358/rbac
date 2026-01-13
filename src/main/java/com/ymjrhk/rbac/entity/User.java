package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable, OptimisticLockEntity {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private String secretToken;

    private String newSecretToken; // 新 secret_token

    private Integer authVersion;

    private LocalDateTime createTime;

    private Long createUserId;

    private LocalDateTime updateTime;

    private Long updateUserId;
}
