package com.bage.finance.biz.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 凭证科目明细表（表：voucher_subject_detail）
 *
 * @author bage
 */
public class VoucherSubjectDetail {
    /**
     * 
     */
    private Long id;

    /**
     * 凭证id
     */
    private Long voucherId;

    /**
     * 科目id
     */
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
     * 是否启用外币核算
     */
    private Boolean enableForeignCurrencyConfig;

    /**
     * 是否启用数量核算
     */
    private Boolean enableNumberCalculateConfig;

    /**
     * 是否启用辅助核算
     */
    private Boolean enableAssistCalculateConfigs;

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
     * 租户id
     */
    private Long tenantId;

    /**
     * 是否禁用
     */
    private Boolean disable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 修改用户id
     */
    private Long updateMemberId;

    /**
     * 是否删除，0：删除，1：未删除
     */
    private Boolean delFlag;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 第几行数据，行编号
     */
    private Integer rowNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getCurrencyConfigId() {
        return currencyConfigId;
    }

    public void setCurrencyConfigId(Long currencyConfigId) {
        this.currencyConfigId = currencyConfigId;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getOriginalCurrency() {
        return originalCurrency;
    }

    public void setOriginalCurrency(BigDecimal originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    public Integer getSubjectNum() {
        return subjectNum;
    }

    public void setSubjectNum(Integer subjectNum) {
        this.subjectNum = subjectNum;
    }

    public BigDecimal getSubjectUnitPrice() {
        return subjectUnitPrice;
    }

    public void setSubjectUnitPrice(BigDecimal subjectUnitPrice) {
        this.subjectUnitPrice = subjectUnitPrice;
    }

    public Boolean getEnableForeignCurrencyConfig() {
        return enableForeignCurrencyConfig;
    }

    public void setEnableForeignCurrencyConfig(Boolean enableForeignCurrencyConfig) {
        this.enableForeignCurrencyConfig = enableForeignCurrencyConfig;
    }

    public Boolean getEnableNumberCalculateConfig() {
        return enableNumberCalculateConfig;
    }

    public void setEnableNumberCalculateConfig(Boolean enableNumberCalculateConfig) {
        this.enableNumberCalculateConfig = enableNumberCalculateConfig;
    }

    public Boolean getEnableAssistCalculateConfigs() {
        return enableAssistCalculateConfigs;
    }

    public void setEnableAssistCalculateConfigs(Boolean enableAssistCalculateConfigs) {
        this.enableAssistCalculateConfigs = enableAssistCalculateConfigs;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getUpdateMemberId() {
        return updateMemberId;
    }

    public void setUpdateMemberId(Long updateMemberId) {
        this.updateMemberId = updateMemberId;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getRowNo() {
        return rowNo;
    }

    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }

    public void initDefault() {
        if (this.getVoucherId() == null) {
            this.setVoucherId(0L);
        }
        if (this.getSubjectId() == null) {
            this.setSubjectId(0L);
        }
        if (this.getCurrencyConfigId() == null) {
            this.setCurrencyConfigId(0L);
        }
        if (this.getExchangeRate() == null) {
            this.setExchangeRate(new BigDecimal("0.00"));
        }
        if (this.getOriginalCurrency() == null) {
            this.setOriginalCurrency(new BigDecimal("0.00"));
        }
        if (this.getSubjectNum() == null) {
            this.setSubjectNum(0);
        }
        if (this.getSubjectUnitPrice() == null) {
            this.setSubjectUnitPrice(new BigDecimal("0.00"));
        }
        if (this.getEnableForeignCurrencyConfig() == null) {
            this.setEnableForeignCurrencyConfig(false);
        }
        if (this.getEnableNumberCalculateConfig() == null) {
            this.setEnableNumberCalculateConfig(false);
        }
        if (this.getEnableAssistCalculateConfigs() == null) {
            this.setEnableAssistCalculateConfigs(false);
        }
        if (this.getBalance() == null) {
            this.setBalance(new BigDecimal("0.00"));
        }
        if (this.getDebitAmount() == null) {
            this.setDebitAmount(new BigDecimal("0.00"));
        }
        if (this.getCreditAmount() == null) {
            this.setCreditAmount(new BigDecimal("0.00"));
        }
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
        }
        if (this.getDisable() == null) {
            this.setDisable(false);
        }
        if (this.getCreateTime() == null) {
            this.setCreateTime(new Date());
        }
        if (this.getUpdateTime() == null) {
            this.setUpdateTime(new Date());
        }
        if (this.getMemberId() == null) {
            this.setMemberId(0L);
        }
        if (this.getUpdateMemberId() == null) {
            this.setUpdateMemberId(0L);
        }
        if (this.getDelFlag() == null) {
            this.setDelFlag(false);
        }
        if (this.getSummary() == null) {
            this.setSummary("");
        }
        if (this.getRowNo() == null) {
            this.setRowNo(0);
        }
    }

    public void initUpdate() {
    }
}