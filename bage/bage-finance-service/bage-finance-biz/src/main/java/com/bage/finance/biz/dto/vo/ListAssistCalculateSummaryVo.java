package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 辅助核算（表：assist_calculate）
 */
@Data
public class ListAssistCalculateSummaryVo {
    /**
     * id
     */
    private Long id;

    /**
     * 辅助核算id
     */
    private Long assistCalculateId;

    /**
     * 辅助核算类别名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 助记码
     */
    private String mnemonicCode;
}