package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "角色创建参数")
// 和 RoleCreateDTO 相比少 id，多 status
public class RoleCreateDTO {
    @Schema(description = "角色名", example = "USER")
    @NotBlank(message = "角色名不能为空")
    private String roleName;

    @Schema(description = "角色显示名称", example = "普通用户")
    @NotBlank(message = "角色显示名称不能为空")
    private String roleDisplayName;

    @Schema(description = "描述", example = "这是一个普通用户")
    private String description;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
