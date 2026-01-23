package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
// 修改用
// 和 RoleCreateDTO 相比少 status
public class RoleDTO {
    private String roleName;

    private String roleDisplayName;

    private String description;

    private Integer version;

    private String secretToken;

//    private Integer status;
}
