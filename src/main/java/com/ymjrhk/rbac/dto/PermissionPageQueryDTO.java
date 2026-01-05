package com.ymjrhk.rbac.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionPageQueryDTO implements Serializable {
    private String permissionName;

    private Integer pageNum;

    private Integer pageSize;

}
