package com.ymjrhk.rbac.dto.auth;

import lombok.Data;

/**
 * 数据库中用户登录所需的验证信息
 * （数据库里现在是谁）
 */
@Data
public class UserAuthInfo {
    private Long userId;
    private String username;
    private Integer status;
    private Integer authVersion;
}
