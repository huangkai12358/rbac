package com.ymjrhk.rbac.controller;

import com.alibaba.excel.EasyExcel;
import com.ymjrhk.rbac.annotation.Audit;
import com.ymjrhk.rbac.dto.AuditLogPageQueryDTO;
import com.ymjrhk.rbac.result.PageResult;
import com.ymjrhk.rbac.result.Result;
import com.ymjrhk.rbac.service.AuditLogService;
import com.ymjrhk.rbac.utils.ExcelStyleUtil;
import com.ymjrhk.rbac.vo.AuditLogExcelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.ymjrhk.rbac.constant.PermissionNameConstant.AUDIT_VIEW;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "审计日志模块")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 审计日志分页查询
     *
     * @param auditLogPageQueryDTO
     * @return
     */
    @Audit(permission = AUDIT_VIEW)
    @GetMapping("/page")
    @Operation(summary = "审计日志分页查询")
    public Result<PageResult> pageQuery(AuditLogPageQueryDTO auditLogPageQueryDTO) {
        log.info("审计日志分页查询，参数为：{}", auditLogPageQueryDTO);
        PageResult pageResult = auditLogService.pageQuery(auditLogPageQueryDTO);
        return Result.success(pageResult);
    }

    @Audit(permission = AUDIT_VIEW)
    @GetMapping("/export")
    @Operation(summary = "审计日志导出")
    public void export(AuditLogPageQueryDTO dto, HttpServletResponse response) {

        List<AuditLogExcelVO> data = auditLogService.listForExport(dto);

        String fileName = "audit-log-" + LocalDate.now();

        try {
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // 解决中文文件名问题
            /*
            - HTTP Header 不支持直接放中文
            - URLEncoder.encode 会把中文转成 %E5%AE%A1...
            - "+" 在文件名里不安全，改成 %20（空格）
            */
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                                               .replaceAll("\\+", "%20");

            // 对于 跨域请求，浏览器只允许 JS 访问 “白名单响应头”
            /*
            默认可访问的响应头（没配 CORS 时）
            - Cache-Control
            - Content-Language
            - Content-Type
            - Expires
            - Last-Modified
            - Pragma
             */
            response.setHeader(
                    "Content-Disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx"
            );

            EasyExcel.write(response.getOutputStream(), AuditLogExcelVO.class)
                     .registerWriteHandler(ExcelStyleUtil.defaultStyle())
                     .sheet("审计日志")
                     .doWrite(data);

        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }

}
