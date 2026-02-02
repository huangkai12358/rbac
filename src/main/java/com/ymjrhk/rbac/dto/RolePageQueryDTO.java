package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageQueryDTO extends PageQuery implements Serializable {

    // 角色名
    private String roleName;

    // 排序字段
    private String sortField;

    // 排序方式：asc / desc
    private String sortOrder;
}
