package com.ymjrhk.rbac.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuditLogCreateDTO implements Serializable {
    private String username;

    private String permissionName;

    private Integer success;

    private LocalDateTime createTime;

    private Integer pageNum;

    private Integer pageSize;
}
