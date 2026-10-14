package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 禁用或启用科目科目
 */
@Data
public class DisableSubjectForm {

    /**
     * 科目id
     */
    @NotNull
    private Long id;

    /**
     * 禁用或启用科目
     */
    @NotNull
    private Boolean disable;
}