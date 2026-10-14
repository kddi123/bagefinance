package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 科目编码长度配置
 */
@Data
public class SubjectCodeLengthConfigVo {
    /**
     * 第一级编码长度
     */
    private Integer depthCodeLengthOne;

    /**
     * 第二级编码长度
     */
    private Integer depthCodeLengthTwo;

    /**
     * 第三级编码长度
     */
    private Integer depthCodeLengthThree;

    /**
     * 第四级编码长度
     */
    private Integer depthCodeLengthFour;
}