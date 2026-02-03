package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "权限导出参数")
public class PermissionExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long permissionId;

    @ExcelProperty(value = "权限标识", index = 1)
    @ColumnWidth(30)
    private String permissionName;

    @ExcelProperty(value = "权限名称", index = 2)
    @ColumnWidth(20)
    private String permissionDisplayName;

    @ExcelProperty(value = "类型", index = 3)
    @ColumnWidth(10)
    private Integer type;

    @ExcelProperty(value = "路径", index = 4)
    @ColumnWidth(30)
    private String path;

    @ExcelProperty(value = "方法", index = 5)
    @ColumnWidth(10)
    private String method;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 7)
    @ColumnWidth(25)
    private LocalDateTime createTime;
}
