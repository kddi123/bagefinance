package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class GetVoucherVo {
    /**
     * id
     */
    private Long id;

    /**
     * 凭证字配置id
     */
    private Long voucherWordConfigId;

    /**
     * 凭证字
     */
    private String voucherWord;

    /**
     * 凭证号
     */
    private Integer voucherNumber;

    /**
     * 凭证日期
     */
    private Date voucherDate;

    /**
     * 单据数量
     */
    private Integer documentNum;

    /**
     * 凭证总金额
     */
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String notes;

    /**
     * 科目明细
     */
    private List<VoucherSubjectDetailVo> voucherSubjectDetailVoList;

    /**
     * 凭证科目明细
     */
    @Data
    public static class VoucherSubjectDetailVo {
        /**
         * 凭证科目明细id
         */
        private Long id;

        /**
         * 凭证id
         */
        private Long voucherId;

        /**
         * 摘要
         */
        private String summary;

        /**
         * 科目id
         */
        private Long subjectId;

        /**
         * 科目编码
         */
        private String subjectCode;

        /**
         * 科目编码+科目名称+辅助核算名称
         */
        private String subjectFullName;

        /**
         * 科目名称
         */
        private String subjectName;

        /**
         * 显示的科目名称
         */
        private String showSubjectName;

        /**
         * 币别id，如果未启用外币辅助核算则为0
         */
        private Long currencyConfigId;

        /**
         * 币别名称
         */
        private String currencyConfigName;

        /**
         * 汇率
         */
        private BigDecimal exchangeRate;

        /**
         * 原币
         */
        private BigDecimal originalCurrency;

        /**
         * 科目对应的数量
         */
        private Integer subjectNum;

        /**
         * 科目对应的单价数量
         */
        private BigDecimal subjectUnitPrice;

        /**
         * 余额
         */
        private BigDecimal balance;

        /**
         * 借方金额
         */
        private BigDecimal debitAmount;

        /**
         * 贷方金额
         */
        private BigDecimal creditAmount;

        /**
         * 是否启用数量核算配置
         */
        private Boolean enableNumberCalculateConfig;

        /**
         * 是否启用辅助核算配置
         */
        private Boolean enableAssistCalculateConfigs;

        /**
         * 是否启用外币核算配置
         */
        private Boolean enableForeignCurrencyConfig;

        /**
         * 辅助核算配置
         */
        private List<AssistCalculateConfigVo> assistCalculateConfigs;

        /**
         * 外币核算配置
         */
        private List<ForeignCurrencyConfigVo> foreignCurrencyConfig;
    }

    /**
     * 外币核算配置
     */
    @Data
    public static class ForeignCurrencyConfigVo {
        /**
         * 币种id
         */
        private Long id;

        /**
         * 币别名称
         */
        private String name;

        /**
         * 汇率
         */
        private BigDecimal exchangeRate;

        /**
         * 是否是本位币
         */
        private Boolean baseCurrencyFlag;
    }

    /**
     * 设置的辅助核算
     */
    @Data
    public static class AssistCalculateConfigVo {
        /**
         * 科目id
         */
        private Long subjectId;

        /**
         * 辅助核算类别id
         */
        private Long assistCalculateCateId;

        /**
         * 辅助核算id
         */
        private Long assistCalculateId;

        /**
         * 辅助核算名称
         */
        private String assistCalculateName;
    }
}
