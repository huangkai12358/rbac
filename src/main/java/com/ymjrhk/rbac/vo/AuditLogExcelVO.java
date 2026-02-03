package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "审计日志导出参数")
public class AuditLogExcelVO {

    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(10)
    private Long logSeq;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "权限名", index = 2)
    @ColumnWidth(30)
    private String permissionName;

    @ExcelProperty(value = "路径", index = 3)
    @ColumnWidth(30)
    private String path;

    @ExcelProperty(value = "方法", index = 4)
    @ColumnWidth(10)
    private String method;

    @ExcelProperty(value = "请求体", index = 5)
    @ColumnWidth(70)
    private String requestBody;

    @ExcelProperty(value = "IP 地址", index = 6)
    @ColumnWidth(20)
    private String ip;

    @ExcelProperty(value = "是否成功", index = 7)
    @ColumnWidth(20)
    private String success; // 注意此处 success 为 String

    @ExcelProperty(value = "错误信息", index = 8)
    @ColumnWidth(20)
    private String errorMessage;

    @ExcelProperty(value = "操作时间", index = 9)
    @ColumnWidth(25)
    private LocalDateTime createTime;
}
