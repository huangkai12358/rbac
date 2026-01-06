package com.ymjrhk.rbac.vo;

import lombok.Data;

@Data
public class UserRoleVO {
    private Long roleId;

    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer status;
}
