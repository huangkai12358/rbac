package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户详情返回参数")
public class UserDetailVO {
    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private String secretToken;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLoginTime;
}
