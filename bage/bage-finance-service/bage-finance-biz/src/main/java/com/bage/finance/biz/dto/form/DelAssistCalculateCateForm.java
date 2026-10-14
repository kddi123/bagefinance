package com.bage.finance.biz.dto.form;

import lombok.Data;

 import jakarta.validation.constraints.NotNull;

/**
 * 删除辅助核算类目
 *
 */
@Data
public class DelAssistCalculateCateForm {

    /**
     * 辅助核算类目id
     */
    @NotNull
    private Long id;
}