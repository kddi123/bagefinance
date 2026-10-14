package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 货币配置（表：currency_config）
 */
@Data
public class ListCurrencyConfigVo {
    /**
     * id
     */
    private Long id;

    /**
     * 编码[RMB，USD]
     */
    private String code;

    /**
     * 币别名称[人民币，美元]
     */
    private String name;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 是否是本位币，0：否，1：是
     */
    private Boolean baseCurrencyFlag;

    /**
     * 使用计数
     */
    private Integer useCount;
}