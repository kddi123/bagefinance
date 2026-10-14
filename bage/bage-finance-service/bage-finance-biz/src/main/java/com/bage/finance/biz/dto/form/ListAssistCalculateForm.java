package com.bage.finance.biz.dto.form;

import com.bage.common.dto.PageHelperRequest;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 查询辅助核算（表：subject）
 */
@Data
public class ListAssistCalculateForm extends PageHelperRequest {

    /**
     * 辅助核算类别id
     */
    @NotNull
    @Min(value = 1)
    private Long assistCalculateCateId;

    /**
     * 编码或名称
     */
    @Size(max = 50)
    private String codeOrName;
}