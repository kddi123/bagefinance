package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ListVoucherVo {
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
         * 科目名称
         */
        private String subjectName;

        /**
         * 科目名称(含父科目名称)
         */
        private String subjectFullName;

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
    }
}
