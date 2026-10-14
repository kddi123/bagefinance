package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 创建币别
 *
 * @date 2023-10-26 00:09:39
 */
@Data
public class CreateCurrencyConfigForm {

    /**
     * 编码[RMB，USD]
     */
    @NotBlank
    private String code;

    /**
     * 币别名称[人民币，美元]
     */
    @NotBlank
    private String name;

    /**
     * 汇率
     */
    @NotNull
    private BigDecimal exchangeRate;
}