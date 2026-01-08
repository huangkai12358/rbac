package com.ymjrhk.rbac.controller;

import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-log")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "审计日志模块")
public class AuditLogController {
    private final AuditLogService auditLogService;

    /**
     * 审计日志分页查询
     * @param auditLogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "审计日志分页查询")
    public Result<PageResult> pageQuery(AuditLogPageQueryDTO auditLogPageQueryDTO) {
        log.info("审计日志分页查询，参数为：{}", auditLogPageQueryDTO);
        PageResult pageResult = auditLogService.pageQuery(auditLogPageQueryDTO);
        return Result.success(pageResult);
    }
}
