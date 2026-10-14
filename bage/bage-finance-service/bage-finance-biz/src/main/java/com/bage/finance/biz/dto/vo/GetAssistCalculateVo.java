package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class GetAssistCalculateVo {
    /**
     * id
     */
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String notes;

    /**
     * 助记码
     */
    private String mnemonicCode;
}