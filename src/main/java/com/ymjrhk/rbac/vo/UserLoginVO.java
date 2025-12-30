package com.ymjrhk.rbac.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginVO {
    private Long userId;
    private String username;
    private String nickname;
    private String token;
}
