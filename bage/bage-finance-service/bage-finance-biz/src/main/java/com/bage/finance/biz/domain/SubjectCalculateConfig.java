package com.bage.finance.biz.domain;


import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 科目核算配置
 */
@Data
public class SubjectCalculateConfig {
    /**
     * 数量核算配置
     */
    private NumberCalculateConfig numberCalculateConfig;

    /**
     * 辅助核算配置
     */
    private List<AssistCalculateConfig> assistCalculateConfigs;

    /**
     * 外币核算配置
     */
    private ForeignCurrencyConfig foreignCurrencyConfig;

    /**
     * 数量核算配置
     */
    @Data
    public static class NumberCalculateConfig {
        /**
         * 计量单位
         */
        private String unitOfMeasurement;
    }

    /**
     * 辅助核算配置
     */
    @Data
    public static class AssistCalculateConfig {
        /**
         * 辅助核算id
         */
        private Long assistCalculateId;

        /**
         * 是否必填，true必填，false非必填
         */
        private Boolean requiredFlag;
    }

    /**
     * 外币核算配置
     */
    @Data
    public static class ForeignCurrencyConfig {
        /**
         * 是否启用期末调汇
         */
        private Boolean endOfYearCurrencyRevaluationFlag;

        /**
         * 币别id列表
         */
        private Set<Long> currencyConfigIds;
    }
}