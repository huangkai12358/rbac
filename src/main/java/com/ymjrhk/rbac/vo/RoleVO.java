package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "角色查询返回参数")
public class RoleVO {
    private Long roleId;
    private String roleName;
    private String roleDisplayName;
    private String description;
    private Integer status;
}
