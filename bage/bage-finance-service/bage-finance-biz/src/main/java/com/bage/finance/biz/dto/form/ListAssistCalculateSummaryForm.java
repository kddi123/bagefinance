package com.bage.finance.biz.dto.form;

import com.bage.common.dto.PageHelperRequest;

import lombok.Data;

import jakarta.validation.constraints.Size;

@Data
public class ListAssistCalculateSummaryForm extends PageHelperRequest {

    /**
     * 编码或名称
     */
    @Size(max = 50)
    private String codeOrName;

    /**
     * 辅助核算类别id
     */
    private Long assistCalculateCateId;
}