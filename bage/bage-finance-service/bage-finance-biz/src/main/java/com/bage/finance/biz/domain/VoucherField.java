package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class VoucherField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField VoucherWordConfigId = new DbField("voucher_word_config_id","voucherWordConfigId","BIGINT","java.lang.Long");

    public static DbField VoucherNumber = new DbField("voucher_number","voucherNumber","INTEGER","java.lang.Integer");

    public static DbField VoucherDate = new DbField("voucher_date","voucherDate","TIMESTAMP","java.util.Date");

    public static DbField DocumentNum = new DbField("document_num","documentNum","INTEGER","java.lang.Integer");

    public static DbField TotalAmount = new DbField("total_amount","totalAmount","DECIMAL","java.math.BigDecimal");

    public static DbField Notes = new DbField("notes","notes","VARCHAR","java.lang.String");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField Version = new DbField("version","version","INTEGER","java.lang.Integer");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setVoucherWordConfigId(Long voucherWordConfigId) {
        return new FieldResult(VoucherWordConfigId, Collections.singletonList(voucherWordConfigId));
    }

    public static FieldResult setVoucherNumber(Integer voucherNumber) {
        return new FieldResult(VoucherNumber, Collections.singletonList(voucherNumber));
    }

    public static FieldResult setVoucherDate(java.util.Date voucherDate) {
        return new FieldResult(VoucherDate, Collections.singletonList(voucherDate));
    }

    public static FieldResult setDocumentNum(Integer documentNum) {
        return new FieldResult(DocumentNum, Collections.singletonList(documentNum));
    }

    public static FieldResult setTotalAmount(java.math.BigDecimal totalAmount) {
        return new FieldResult(TotalAmount, Collections.singletonList(totalAmount));
    }

    public static FieldResult setNotes(String notes) {
        return new FieldResult(Notes, Collections.singletonList(notes));
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

    public static FieldResult setVersion(Integer version) {
        return new FieldResult(Version, Collections.singletonList(version));
    }
}