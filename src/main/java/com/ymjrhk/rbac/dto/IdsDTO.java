package com.ymjrhk.rbac.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "id 列表参数")
public class IdsDTO {
    @Schema(description = "id 列表", example = "[1,2,3]")
    @NotEmpty(message = "id 列表不能为空")
    private List<Long> ids;
}
