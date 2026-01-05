package com.ymjrhk.rbac.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQueryDTO implements Serializable {

    // 用户名
    private String username;

    // 页码
    private Integer pageNum;

    // 每页显示记录数
    private Integer pageSize;

}
