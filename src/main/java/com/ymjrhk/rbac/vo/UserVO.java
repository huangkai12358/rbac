package com.ymjrhk.rbac.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询返回参数")
public class UserVO {
    private Long userId;

    private String username;

    private String nickname;

    private String email;

    private Integer status;
    // TODO: 注册时间/修改时间？
}
