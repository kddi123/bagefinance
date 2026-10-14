package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class AssistCalculateInventoryField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField Specifications = new DbField("specifications","specifications","VARCHAR","java.lang.String");

    public static DbField InventoryCate = new DbField("inventory_cate","inventoryCate","VARCHAR","java.lang.String");

    public static DbField Units = new DbField("units","units","VARCHAR","java.lang.String");

    public static DbField StartDate = new DbField("start_date","startDate","TIMESTAMP","java.util.Date");

    public static DbField EndDate = new DbField("end_date","endDate","TIMESTAMP","java.util.Date");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField AssistCalculateSummaryId = new DbField("assist_calculate_summary_id","assistCalculateSummaryId","BIGINT","java.lang.Long");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setSpecifications(String specifications) {
        return new FieldResult(Specifications, Collections.singletonList(specifications));
    }

    public static FieldResult setInventoryCate(String inventoryCate) {
        return new FieldResult(InventoryCate, Collections.singletonList(inventoryCate));
    }

    public static FieldResult setUnits(String units) {
        return new FieldResult(Units, Collections.singletonList(units));
    }

    public static FieldResult setStartDate(java.util.Date startDate) {
        return new FieldResult(StartDate, Collections.singletonList(startDate));
    }

    public static FieldResult setEndDate(java.util.Date endDate) {
        return new FieldResult(EndDate, Collections.singletonList(endDate));
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

    public static FieldResult setAssistCalculateSummaryId(Long assistCalculateSummaryId) {
        return new FieldResult(AssistCalculateSummaryId, Collections.singletonList(assistCalculateSummaryId));
    }
}