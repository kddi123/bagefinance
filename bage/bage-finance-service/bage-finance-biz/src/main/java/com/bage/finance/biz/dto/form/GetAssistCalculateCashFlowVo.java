package com.bage.finance.biz.dto.form;


import lombok.Data;

@Data
public class GetAssistCalculateCashFlowVo {
    /**
     * id
     */
    private Integer id;

    /**
     * 现金流编码
     */
    private String code;

    /**
     * 现金流名称
     */
    private String name;

    /**
     * 现金流类别
     */
    private String cashFlowCate;

    /**
     * 备注
     */
    private String notes;
}