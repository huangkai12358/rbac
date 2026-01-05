package com.ymjrhk.rbac.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RolePageQueryDTO implements Serializable {
    private String roleName;

    private Integer pageNum;

    private Integer pageSize;

}
