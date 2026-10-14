package com.bage.finance.biz.dto.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * 科目（表：subject）
 */
@Data
public class DownloadSubjectVo {
    /**
     * id
     */
    @ExcelIgnore
    private Long id;

    /**
     * 科目编码
     */
    @ExcelProperty("科目编码")
    @ColumnWidth(20)
    private String code;

    /**
     * 科目名称
     */
    @ExcelProperty("科目名称")
    @ColumnWidth(20)
    private String name;

    /**
     * 上级科目
     */
    @ExcelProperty("上级科目")
    @ColumnWidth(20)
    private String parentSubjectName;

    /**
     * 余额方向
     */
    @ExcelProperty("余额方向")
    @ColumnWidth(20)
    private String balanceDirectionText;

    /**
     * 1：资产，2：负债，3：权益，4：成本，5：损益
     */
    @ExcelProperty("类别")
    @ColumnWidth(20)
    private String subjectCateText;

    /**
     * 计量单位
     */
    @ExcelProperty("数量核算")
    @ColumnWidth(20)
    private String unitOfMeasurement;

    /**
     * 辅助核算列表
     */
    @ExcelProperty("辅助核算")
    @ColumnWidth(20)
    private String assistCalculateText;

    /**
     * 外币核算列表
     */
    @ExcelProperty("外币核算")
    @ColumnWidth(20)
    private String currencyConfigText;

    /**
     * 是否启用期末调汇
     */
    @ExcelProperty("期末调汇")
    @ColumnWidth(20)
    private String endOfYearCurrencyRevaluationFlagText;
}