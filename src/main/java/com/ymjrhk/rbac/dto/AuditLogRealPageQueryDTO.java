package com.ymjrhk.rbac.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实际的审计日志分页查询DTO，把日期转化成了具体时间
 */
@Data
public class AuditLogRealPageQueryDTO implements Serializable {
    private String username;

    private String permissionName;

    private Integer success;

    // 开始时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime startTime;
    // 结束时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 只有前端 query 需要
    private LocalDateTime endTime;

    private Integer pageNum;

    private Integer pageSize;
}
