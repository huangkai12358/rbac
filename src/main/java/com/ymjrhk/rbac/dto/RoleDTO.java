package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
// 修改用
// 和 RoleCreateDTO 相比多 id，少 status
public class RoleDTO {
    private Long roleId;

    private String roleName;

    private String roleDisplayName;

    private String description;

//    private Integer status;
}
