package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class AssistCalculateCustomField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField C1 = new DbField("c1","c1","VARCHAR","java.lang.String");

    public static DbField C2 = new DbField("c2","c2","VARCHAR","java.lang.String");

    public static DbField C3 = new DbField("c3","c3","VARCHAR","java.lang.String");

    public static DbField C4 = new DbField("c4","c4","VARCHAR","java.lang.String");

    public static DbField C5 = new DbField("c5","c5","VARCHAR","java.lang.String");

    public static DbField C6 = new DbField("c6","c6","VARCHAR","java.lang.String");

    public static DbField C7 = new DbField("c7","c7","VARCHAR","java.lang.String");

    public static DbField C8 = new DbField("c8","c8","VARCHAR","java.lang.String");

    public static DbField C9 = new DbField("c9","c9","VARCHAR","java.lang.String");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField C10 = new DbField("c10","c10","VARCHAR","java.lang.String");

    public static DbField AssistCalculateSummaryId = new DbField("assist_calculate_summary_id","assistCalculateSummaryId","BIGINT","java.lang.Long");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setC1(String c1) {
        return new FieldResult(C1, Collections.singletonList(c1));
    }

    public static FieldResult setC2(String c2) {
        return new FieldResult(C2, Collections.singletonList(c2));
    }

    public static FieldResult setC3(String c3) {
        return new FieldResult(C3, Collections.singletonList(c3));
    }

    public static FieldResult setC4(String c4) {
        return new FieldResult(C4, Collections.singletonList(c4));
    }

    public static FieldResult setC5(String c5) {
        return new FieldResult(C5, Collections.singletonList(c5));
    }

    public static FieldResult setC6(String c6) {
        return new FieldResult(C6, Collections.singletonList(c6));
    }

    public static FieldResult setC7(String c7) {
        return new FieldResult(C7, Collections.singletonList(c7));
    }

    public static FieldResult setC8(String c8) {
        return new FieldResult(C8, Collections.singletonList(c8));
    }

    public static FieldResult setC9(String c9) {
        return new FieldResult(C9, Collections.singletonList(c9));
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

    public static FieldResult setTenantId(Long tenantId) {
        return new FieldResult(TenantId, Collections.singletonList(tenantId));
    }

    public static FieldResult setC10(String c10) {
        return new FieldResult(C10, Collections.singletonList(c10));
    }

    public static FieldResult setAssistCalculateSummaryId(Long assistCalculateSummaryId) {
        return new FieldResult(AssistCalculateSummaryId, Collections.singletonList(assistCalculateSummaryId));
    }
}