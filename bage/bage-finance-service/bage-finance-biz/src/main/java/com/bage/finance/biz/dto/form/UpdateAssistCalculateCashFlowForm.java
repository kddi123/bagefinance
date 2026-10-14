package com.bage.finance.biz.dto.form;


import lombok.Data;

/**
 * 辅助核算（现金流）
 */
@Data
public class UpdateAssistCalculateCashFlowForm extends UpdateAssistCalculateForm {

    /**
     * 现金流类别
     */
    private String cashFlowCate;
}