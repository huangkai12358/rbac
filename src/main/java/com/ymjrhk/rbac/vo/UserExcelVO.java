package com.ymjrhk.rbac.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户导出参数")
public class UserExcelVO {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(10)
    private Long userId;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "昵称", index = 2)
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "邮箱", index = 3)
    @ColumnWidth(30)
    private String email;

    @ExcelProperty(value = "状态", index = 4)
    @ColumnWidth(10)
    private String status; // 注意此处 status 为 String

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private LocalDateTime createTime;

    @ExcelProperty(value = "修改时间", index = 6)
    @ColumnWidth(25)
    private LocalDateTime updateTime;
}
