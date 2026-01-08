package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long logSeq;

    private Long userId;

    private String username;

    private String permissionId;

    private String permissionName;

    private String path;

    private String method;

    private String requestBody;

    private String ip;

    private Integer success;

    private String errorMessage;

    private LocalDateTime createTime;
}
