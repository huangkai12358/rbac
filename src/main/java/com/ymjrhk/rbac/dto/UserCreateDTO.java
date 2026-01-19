package com.ymjrhk.rbac.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户创建参数")
public class UserCreateDTO {
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @JSONField(ordinal = 1) // Fastjson 注解指定反序列化顺序
    private String username;

    @Schema(description = "昵称", example = "至尊宝之泪")
    @JSONField(ordinal = 2)
    private String nickname;

    @Schema(description = "邮箱", example = "111@qq.com")
    @JSONField(ordinal = 3)
    private String email;
}
