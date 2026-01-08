package com.ymjrhk.rbac.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AuditLogPageQueryDTO implements Serializable {
    private String username;

    private String permissionName;

    private Integer success;

    // 只接收日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer pageNum;

    private Integer pageSize;
}
