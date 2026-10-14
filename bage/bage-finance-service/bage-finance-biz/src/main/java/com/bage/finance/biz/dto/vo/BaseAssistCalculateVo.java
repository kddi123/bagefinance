package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 辅助核算
 */
@Data
public class BaseAssistCalculateVo {

    /**
     * id
     */
    private Long id;

    /**
     * 辅助核算类别名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 备注
     */
    private String notes;

    /**
     * 助记码
     */
    private String mnemonicCode;

    /**
     * 禁用启用状态，true禁用，false启用
     */
    private Boolean disable;

    /**
     * 辅助核算id
     */
    private Long assistCalculateSummaryId;
}