package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

/**
 * 修改凭证字默认值
 */
@Data
public class UpdateVoucherWordConfigDefaultFlagForm {
    /**
     * 凭证字Id
     */
    @NotNull
    @Range(min = 1)
    private Long id;
}