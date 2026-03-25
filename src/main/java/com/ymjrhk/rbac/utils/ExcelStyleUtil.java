package com.ymjrhk.rbac.utils;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel 样式工具类
 * <p>
 * 用于统一定义 EasyExcel 导出时的单元格样式，
 * 包括表头样式和内容样式，避免在各个导出接口中重复写样式代码。
 */
public class ExcelStyleUtil {

    /**
     * 默认 Excel 样式策略
     * <p>
     * 样式约定：
     * 1. 表头：水平居中、垂直居中、加粗
     * 2. 内容：水平左对齐、垂直居中
     *
     * @return HorizontalCellStyleStrategy EasyExcel 样式策略
     */
    public static HorizontalCellStyleStrategy defaultStyle() {

        // =========================
        // 表头（Header）样式设置
        // =========================

        // 表头单元格样式
        WriteCellStyle headStyle = new WriteCellStyle();
        // 水平居中
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 表头字体样式
        WriteFont headFont = new WriteFont();
        // 字体加粗
        headFont.setBold(true);
        headStyle.setWriteFont(headFont);

        // =========================
        // 内容（Body）样式设置
        // =========================

        // 内容单元格样式
        WriteCellStyle contentStyle = new WriteCellStyle();
        // 水平左对齐（适合文本内容）
        contentStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        // 垂直居中
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 返回 EasyExcel 的表头 + 内容样式组合策略
        return new HorizontalCellStyleStrategy(headStyle, contentStyle);
    }
}