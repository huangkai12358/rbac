package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "id 列表参数")
public class IdsDTO {
    @Schema(description = "id 列表", example = "[1,2,3]")
    @NotNull(message = "id 列表不能为 null")
    // 列表可以为空列表，表示清空角色或权限
    private List<Long> ids;
}
