package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class VoucherSubjectAssistCalculateDetailField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField VoucherId = new DbField("voucher_id","voucherId","BIGINT","java.lang.Long");

    public static DbField SubjectId = new DbField("subject_id","subjectId","BIGINT","java.lang.Long");

    public static DbField VoucherSubjectDetailId = new DbField("voucher_subject_detail_id","voucherSubjectDetailId","BIGINT","java.lang.Long");

    public static DbField AssistCalculateCateId = new DbField("assist_calculate_cate_id","assistCalculateCateId","BIGINT","java.lang.Long");

    public static DbField AssistCalculateCateCode = new DbField("assist_calculate_cate_code","assistCalculateCateCode","VARCHAR","java.lang.String");

    public static DbField AssistCalculateId = new DbField("assist_calculate_id","assistCalculateId","BIGINT","java.lang.Long");

    public static DbField AssistCalculateCode = new DbField("assist_calculate_code","assistCalculateCode","VARCHAR","java.lang.String");

    public static DbField AssistCalculateName = new DbField("assist_calculate_name","assistCalculateName","BIGINT","java.lang.Long");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

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

    public static FieldResult setVoucherSubjectDetailId(Long voucherSubjectDetailId) {
        return new FieldResult(VoucherSubjectDetailId, Collections.singletonList(voucherSubjectDetailId));
    }

    public static FieldResult setAssistCalculateCateId(Long assistCalculateCateId) {
        return new FieldResult(AssistCalculateCateId, Collections.singletonList(assistCalculateCateId));
    }

    public static FieldResult setAssistCalculateCateCode(String assistCalculateCateCode) {
        return new FieldResult(AssistCalculateCateCode, Collections.singletonList(assistCalculateCateCode));
    }

    public static FieldResult setAssistCalculateId(Long assistCalculateId) {
        return new FieldResult(AssistCalculateId, Collections.singletonList(assistCalculateId));
    }

    public static FieldResult setAssistCalculateCode(String assistCalculateCode) {
        return new FieldResult(AssistCalculateCode, Collections.singletonList(assistCalculateCode));
    }

    public static FieldResult setAssistCalculateName(Long assistCalculateName) {
        return new FieldResult(AssistCalculateName, Collections.singletonList(assistCalculateName));
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

    public static FieldResult setRowNo(Integer rowNo) {
        return new FieldResult(RowNo, Collections.singletonList(rowNo));
    }
}