package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

@Data
public class UpdateDisableForm {
    /**
     * 客户id
     */
    @NotNull
    @Range(min = 1)
    private Long id;

    /**
     * 禁用或启用角色，true禁用，false启用
     */
    @NotNull
    private Boolean disable;
}
