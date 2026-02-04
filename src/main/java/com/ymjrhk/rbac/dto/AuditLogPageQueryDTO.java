package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogPageQueryDTO extends PageQuery implements Serializable {

    private Long logSeq;

    private String username;

    private String permissionName;

    private Integer success;

    // 只接收日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // 前端传：排序字段、排序方向
    private String sortField; // e.g. "createTime", "username", "ip"

    private String sortOrder; // "asc" / "desc"

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
}
