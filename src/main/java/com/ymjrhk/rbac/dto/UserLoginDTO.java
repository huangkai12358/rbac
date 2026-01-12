package com.ymjrhk.rbac.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录参数")
public class UserLoginDTO {
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @JSONField(ordinal = 1) // Fastjson 注解指定反序列化顺序
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @JSONField(ordinal = 2)
    private String password;
}
