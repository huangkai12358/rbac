package com.ymjrhk.rbac.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RoleHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Integer version;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;

    private String secretToken;

    private Integer operateType;

    private LocalDateTime operateTime;

    private Long operatorId;
}
