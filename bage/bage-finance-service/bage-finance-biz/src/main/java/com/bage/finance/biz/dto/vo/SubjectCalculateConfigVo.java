package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 科目核算配置
 */
@Data
public class SubjectCalculateConfigVo {
    /**
     * 是否启用数量核算配置
     */
    private Boolean enableNumberCalculateConfig;
    /**
     * 数量核算配置是否从上级科目继承过来,true表示继承，前端无需传递
     */
    private Boolean extendParentNumberCalculateConfigFlag;
    /**
     * 数量核算配置
     */
    private NumberCalculateConfig numberCalculateConfig;

    /**
     * 是否启用辅助核算配置
     */
    private Boolean enableAssistCalculateConfigs;
    /**
     * 辅助核算配置是否从上级科目继承过来，true表示继承，前端无需传递
     */
    private Boolean extendParentAssistCalculateConfigsFlag;
    /**
     * 辅助核算配置
     */
    private List<AssistCalculateConfig> assistCalculateConfigs;

    /**
     * 是否启用外币核算配置
     */
    private Boolean enableForeignCurrencyConfig;
    /**
     * 外币辅助核算配置是否从上级科目继承过来，true表示继承，前端无需传递
     */
    private Boolean extendParentForeignCurrencyConfigFlag;
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

        /**
         * 从上级科目继承过来的币别id列表
         */
        private Set<Long> parentSubjectCurrencyConfigIds;
    }
}