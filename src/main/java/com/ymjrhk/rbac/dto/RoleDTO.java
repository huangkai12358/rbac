package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private String roleDisplayName;
    private String description;
//    private Integer status;
}
