package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "角色导出参数")
public class RoleExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long roleId;

    @ExcelProperty(value = "角色标识", index = 1)
    @ColumnWidth(20)
    private String roleName;

    @ExcelProperty(value = "角色名称", index = 2)
    @ColumnWidth(20)
    private String roleDisplayName;

    @ExcelProperty(value = "描述", index = 3)
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private LocalDateTime createTime;
}
