package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 科目（表：subject）
 *
 */
@Data
public class GetSubjectDetailVo {
    /**
     *id
     */
    private Long id;

    /**
     *父类id
     */
    private Long pid;

    /**
     *父类名称
     */
    private String parentName;


    /**
     *科目编码
     */
    private String code;

    /**
     *科目名称
     */
    private String name;

    /**
     *1：资产，2：负债，3：权益，4：成本，5：损益
     */
    private Integer subjectCate;

    /**
     *科目类别名称
     */
    private String subjectCateName;

    /**
     *科目余额
     */
    // todo 后面需要统计余额返回给前端
    private BigDecimal subjectBalance = BigDecimal.ZERO;

    /**
     *科目核算配置
     */
    private SubjectCalculateDetailConfigVo subjectCalculateConfigVo;

    @Data
    public static class SubjectCalculateDetailConfigVo {
        /**
         *是否启用数量核算配置
         */
        private Boolean enableNumberCalculateConfig;

        /**
         *是否启用辅助核算配置
         */
        private Boolean enableAssistCalculateConfigs;

        /**
         *是否启用外币核算配置
         */
        private Boolean enableForeignCurrencyConfig;

        /**
         *外币核算配置
         */
        private List<ForeignCurrencyConfigVo> foreignCurrencyConfig;

        /**
         *辅助核算配置
         */
        private List<AssistCalculateConfigVo> assistCalculateConfigs;

    }

    /**
     * 外币核算配置
     */
    @Data
    public static class ForeignCurrencyConfigVo {
        /**
         *币种id
         */
        private Long id;

        /**
         *币别名称
         */
        private String name;

        /**
         *汇率
         */
        private BigDecimal exchangeRate;

        /**
         *是否是本位币
         */
        private Boolean baseCurrencyFlag;
    }

    /**
     * 辅助核算配置
     */
    @Data
    public static class AssistCalculateConfigVo {
        /**
         *辅助核算类别id
         */
        private Long id;

        /**
         *辅助核算类别名称
         */
        private String name;

        /**
         *辅助核算类别编码
         */
        private String code;

        /**
         *是否必填，true必填，false非必填
         */
        private Boolean requiredFlag;
    }
}