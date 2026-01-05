package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "用户登录返回参数")
public class UserLoginVO {
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    private String nickname;

    private String token;
}
