package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class VoucherSubjectDetailField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField VoucherId = new DbField("voucher_id","voucherId","BIGINT","java.lang.Long");

    public static DbField SubjectId = new DbField("subject_id","subjectId","BIGINT","java.lang.Long");

    public static DbField CurrencyConfigId = new DbField("currency_config_id","currencyConfigId","BIGINT","java.lang.Long");

    public static DbField ExchangeRate = new DbField("exchange_rate","exchangeRate","DECIMAL","java.math.BigDecimal");

    public static DbField OriginalCurrency = new DbField("original_currency","originalCurrency","DECIMAL","java.math.BigDecimal");

    public static DbField SubjectNum = new DbField("subject_num","subjectNum","INTEGER","java.lang.Integer");

    public static DbField SubjectUnitPrice = new DbField("subject_unit_price","subjectUnitPrice","DECIMAL","java.math.BigDecimal");

    public static DbField EnableForeignCurrencyConfig = new DbField("enable_foreign_currency_config","enableForeignCurrencyConfig","BIT","java.lang.Boolean");

    public static DbField EnableNumberCalculateConfig = new DbField("enable_number_calculate_config","enableNumberCalculateConfig","BIT","java.lang.Boolean");

    public static DbField EnableAssistCalculateConfigs = new DbField("enable_assist_calculate_configs","enableAssistCalculateConfigs","BIT","java.lang.Boolean");

    public static DbField Balance = new DbField("balance","balance","DECIMAL","java.math.BigDecimal");

    public static DbField DebitAmount = new DbField("debit_amount","debitAmount","DECIMAL","java.math.BigDecimal");

    public static DbField CreditAmount = new DbField("credit_amount","creditAmount","DECIMAL","java.math.BigDecimal");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField Summary = new DbField("summary","summary","VARCHAR","java.lang.String");

    public static DbField RowNo = new DbField("row_no","rowNo","INTEGER","java.lang.Integer");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setVoucherId(Long voucherId) {
        return new FieldResult(VoucherId, Collections.singletonList(voucherId));
    }

    public static FieldResult setSubjectId(Long subjectId) {
        return new FieldResult(SubjectId, Collections.singletonList(subjectId));
    }

    public static FieldResult setCurrencyConfigId(Long currencyConfigId) {
        return new FieldResult(CurrencyConfigId, Collections.singletonList(currencyConfigId));
    }

    public static FieldResult setExchangeRate(java.math.BigDecimal exchangeRate) {
        return new FieldResult(ExchangeRate, Collections.singletonList(exchangeRate));
    }

    public static FieldResult setOriginalCurrency(java.math.BigDecimal originalCurrency) {
        return new FieldResult(OriginalCurrency, Collections.singletonList(originalCurrency));
    }

    public static FieldResult setSubjectNum(Integer subjectNum) {
        return new FieldResult(SubjectNum, Collections.singletonList(subjectNum));
    }

    public static FieldResult setSubjectUnitPrice(java.math.BigDecimal subjectUnitPrice) {
        return new FieldResult(SubjectUnitPrice, Collections.singletonList(subjectUnitPrice));
    }

    public static FieldResult setEnableForeignCurrencyConfig(Boolean enableForeignCurrencyConfig) {
        return new FieldResult(EnableForeignCurrencyConfig, Collections.singletonList(enableForeignCurrencyConfig));
    }

    public static FieldResult setEnableNumberCalculateConfig(Boolean enableNumberCalculateConfig) {
        return new FieldResult(EnableNumberCalculateConfig, Collections.singletonList(enableNumberCalculateConfig));
    }

    public static FieldResult setEnableAssistCalculateConfigs(Boolean enableAssistCalculateConfigs) {
        return new FieldResult(EnableAssistCalculateConfigs, Collections.singletonList(enableAssistCalculateConfigs));
    }

    public static FieldResult setBalance(java.math.BigDecimal balance) {
        return new FieldResult(Balance, Collections.singletonList(balance));
    }

    public static FieldResult setDebitAmount(java.math.BigDecimal debitAmount) {
        return new FieldResult(DebitAmount, Collections.singletonList(debitAmount));
    }

    public static FieldResult setCreditAmount(java.math.BigDecimal creditAmount) {
        return new FieldResult(CreditAmount, Collections.singletonList(creditAmount));
    }

    public static FieldResult setTenantId(Long tenantId) {
        return new FieldResult(TenantId, Collections.singletonList(tenantId));
    }

    public static FieldResult setDisable(Boolean disable) {
        return new FieldResult(Disable, Collections.singletonList(disable));
    }

    public static FieldResult setCreateTime(java.util.Date createTime) {
        return new FieldResult(CreateTime, Collections.singletonList(createTime));
    }

    public static FieldResult setUpdateTime(java.util.Date updateTime) {
        return new FieldResult(UpdateTime, Collections.singletonList(updateTime));
    }

    public static FieldResult setMemberId(Long memberId) {
        return new FieldResult(MemberId, Collections.singletonList(memberId));
    }

    public static FieldResult setUpdateMemberId(Long updateMemberId) {
        return new FieldResult(UpdateMemberId, Collections.singletonList(updateMemberId));
    }

    public static FieldResult setDelFlag(Boolean delFlag) {
        return new FieldResult(DelFlag, Collections.singletonList(delFlag));
    }

    public static FieldResult setSummary(String summary) {
        return new FieldResult(Summary, Collections.singletonList(summary));
    }

    public static FieldResult setRowNo(Integer rowNo) {
        return new FieldResult(RowNo, Collections.singletonList(rowNo));
    }
}