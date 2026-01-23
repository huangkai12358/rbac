package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户分页查询返回参数")
public class UserVO {
    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private Integer status;

    private Integer version;

    private String secretToken;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

//    private LocalDateTime lastLoginTime;

    // TODO: 注册时间/修改时间？
}
