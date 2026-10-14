package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 创建凭证
 */
@Data
public class CreateVoucherForm {

    /**
     * 凭证id，不为空且大于0表示修改凭证，否则是创建凭证
     */
    @Min(value = 0)
    private Long id;

    /**
     * 凭证字配置id
     */
    @NotNull
    @Min(value = 1)
    private Long voucherWordConfigId;

    /**
     * 凭证号
     */
    @NotNull
    @Min(value = 1)
    private Integer voucherNumber;

    /**
     * 凭证日期
     */
    @NotNull
    private Date voucherDate;

    /**
     * 单据数量
     */
    @NotNull
    @Min(value = 0)
    private Integer documentNum;

    /**
     * 凭证总金额
     */
    @NotNull
    @Min(value = 1)
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String notes;

    /**
     * 文件id列表
     */
    private Set<Long> fileIds;

    /**
     * 科目明细
     */
    @NotNull
    @Size(min = 2, max = 100)
    @Valid
    List<VoucherSubjectDetailForm> voucherSubjectDetailFormList;

    /**
     * 辅助核算明细
     */
    @Valid
    List<VoucherSubjectAssistCalculateDetailForm> voucherSubjectAssistCalculateDetailFormList;

    /**
     * 凭证科目明细
     */
    @Data
    public static class VoucherSubjectDetailForm {

        /**
         * 行编号
         */
        @NotNull
        @Min(value = 1)
        private Integer rowNo;

        /**
         * 摘要
         */
        @NotBlank
        private String summary;

        /**
         * 科目id
         */
        @NotNull
        @Min(value = 1)
        private Long subjectId;

        /**
         * 币别id，如果未启用外币辅助核算则为0
         */
        private Long currencyConfigId;

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

    @Data
    public static class VoucherSubjectAssistCalculateDetailForm {

        /**
         * 行编号
         */
        @NotNull
        @Min(value = 1)
        private Integer rowNo;

        /**
         * 科目id
         */
        @NotNull
        @Range(min = 1)
        private Long subjectId;

        /**
         * 辅助核算类别id
         */
        @NotNull
        @Range(min = 1)
        private Long assistCalculateCateId;

        /**
         * 辅助核算id
         */
        @NotNull
        @Range(min = 1)
        private Long assistCalculateId;
    }
}
