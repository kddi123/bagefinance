package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 删除币别
 *
 * @date 2023-10-26 00:09:39
 */
@Data
public class DelCurrencyConfigForm {

    /**
     * 币别id
     */
    @NotNull
    private Long id;
}