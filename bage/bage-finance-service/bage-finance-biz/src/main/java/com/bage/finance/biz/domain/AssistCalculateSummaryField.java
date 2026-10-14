package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class AssistCalculateSummaryField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField Name = new DbField("name","name","VARCHAR","java.lang.String");

    public static DbField Code = new DbField("code","code","VARCHAR","java.lang.String");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField MnemonicCode = new DbField("mnemonic_code","mnemonicCode","VARCHAR","java.lang.String");

    public static DbField AssistCalculateCateId = new DbField("assist_calculate_cate_id","assistCalculateCateId","BIGINT","java.lang.Long");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField AssistCalculateCateCode = new DbField("assist_calculate_cate_code","assistCalculateCateCode","VARCHAR","java.lang.String");

    public static DbField Notes = new DbField("notes","notes","VARCHAR","java.lang.String");

    public static DbField UseCount = new DbField("use_count","useCount","INTEGER","java.lang.Integer");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setName(String name) {
        return new FieldResult(Name, Collections.singletonList(name));
    }

    public static FieldResult setCode(String code) {
        return new FieldResult(Code, Collections.singletonList(code));
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

    public static FieldResult setMnemonicCode(String mnemonicCode) {
        return new FieldResult(MnemonicCode, Collections.singletonList(mnemonicCode));
    }

    public static FieldResult setAssistCalculateCateId(Long assistCalculateCateId) {
        return new FieldResult(AssistCalculateCateId, Collections.singletonList(assistCalculateCateId));
    }

    public static FieldResult setTenantId(Long tenantId) {
        return new FieldResult(TenantId, Collections.singletonList(tenantId));
    }

    public static FieldResult setAssistCalculateCateCode(String assistCalculateCateCode) {
        return new FieldResult(AssistCalculateCateCode, Collections.singletonList(assistCalculateCateCode));
    }

    public static FieldResult setNotes(String notes) {
        return new FieldResult(Notes, Collections.singletonList(notes));
    }

    public static FieldResult setUseCount(Integer useCount) {
        return new FieldResult(UseCount, Collections.singletonList(useCount));
    }
}