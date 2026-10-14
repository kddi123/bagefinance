package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 账套（表：account_book）
 */
@Data
public class AccountBookDisableForm {
    /**
     * 账套id
     */
    @NotNull
    @Min(value = 1)
    private Long id;

    /**
     * true禁用，false：启用
     */
    @NotNull
    private Boolean disable;
}