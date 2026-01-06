package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long roleId;

    private LocalDateTime createTime;

    private Long createUserId;
}
