package com.ymjrhk.rbac.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "个人密码修改参数")
public class MePasswordUpdateDTO {

    @Schema(description = "原密码", example = "123456")
    @NotBlank(message = "原密码不能为空")
    @JSONField(ordinal = 1) // Fastjson 注解指定反序列化顺序
    private String oldPassword;

    @Schema(description = "新密码", example = "12345678")
    @NotBlank(message = "新密码不能为空")
    @JSONField(ordinal = 2)
    private String newPassword;
}
