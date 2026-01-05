package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "权限创建参数")
public class PermissionCreateDTO {
    @Schema(description = "权限名", example = "system:user:create")
    @NotBlank(message = "权限名不能为空")
    private String permissionName;

    @Schema(description = "权限显示名称", example = "创建用户")
    @NotBlank(message = "权限显示名称不能为空")
    private String permissionDisplayName;

    @Schema(description = "描述", example = "这个权限可以创建用户")
    private String description;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "类型", example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "父权限ID", example = "0")
    private Long parentId;

    @Schema(description = "请求路径", example = "/api/user")
    private String path;

    @Schema(description = "方法", example = "POST")
    private String method;

    @Schema(description = "排序", example = "1")
    private Integer sort;
}
