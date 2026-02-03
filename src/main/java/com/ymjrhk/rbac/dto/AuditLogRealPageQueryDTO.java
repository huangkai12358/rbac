package com.ymjrhk.rbac.dto;

import com.ymjrhk.rbac.dto.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实际的审计日志分页查询DTO，把日期转化成了具体时间
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogRealPageQueryDTO extends PageQuery implements Serializable {

    private String username;

    private String permissionName;

    private Integer success;

    // 开始时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime startTime;
    // 结束时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime endTime;

    // 前端传：排序字段、排序方向
    private String sortField; // e.g. "createTime", "username", "ip"

    private String sortOrder; // "asc" / "desc"

    // 后端生成：安全的 order by 子句（数据库字段）
    private String orderBy;
}
