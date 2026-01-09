package com.ymjrhk.rbac.dto;

import lombok.Data;

@Data
// 修改用
public class UserDTO {
    private String username;

    private String nickname;

    private String email;

//    private Integer status; // TODO: 要取消status吗
}
