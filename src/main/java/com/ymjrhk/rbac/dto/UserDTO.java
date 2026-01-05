package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
//    private Integer status; // TODO: 要取消status吗
}
