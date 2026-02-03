package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQueryDTO extends PageQuery implements Serializable {

    // 用户名
    private String username;

    // 前端传：排序字段
    private String sortField;

    // 前端传：排序方式：asc / desc
    private String sortOrder;

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
}

