package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class AssistCalculateSupplierField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField SupplierCate = new DbField("supplier_cate","supplierCate","VARCHAR","java.lang.String");

    public static DbField Address = new DbField("address","address","VARCHAR","java.lang.String");

    public static DbField Contacts = new DbField("contacts","contacts","VARCHAR","java.lang.String");

    public static DbField Phone = new DbField("phone","phone","VARCHAR","java.lang.String");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField UnifiedSocialCreditCode = new DbField("unified_social_credit_code","unifiedSocialCreditCode","VARCHAR","java.lang.String");

    public static DbField ProvinceCode = new DbField("province_code","provinceCode","VARCHAR","java.lang.String");

    public static DbField CityCode = new DbField("city_code","cityCode","VARCHAR","java.lang.String");

    public static DbField CountyCode = new DbField("county_code","countyCode","VARCHAR","java.lang.String");

    public static DbField AssistCalculateSummaryId = new DbField("assist_calculate_summary_id","assistCalculateSummaryId","BIGINT","java.lang.Long");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setSupplierCate(String supplierCate) {
        return new FieldResult(SupplierCate, Collections.singletonList(supplierCate));
    }

    public static FieldResult setAddress(String address) {
        return new FieldResult(Address, Collections.singletonList(address));
    }

    public static FieldResult setContacts(String contacts) {
        return new FieldResult(Contacts, Collections.singletonList(contacts));
    }

    public static FieldResult setPhone(String phone) {
        return new FieldResult(Phone, Collections.singletonList(phone));
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

    public static FieldResult setUnifiedSocialCreditCode(String unifiedSocialCreditCode) {
        return new FieldResult(UnifiedSocialCreditCode, Collections.singletonList(unifiedSocialCreditCode));
    }

    public static FieldResult setProvinceCode(String provinceCode) {
        return new FieldResult(ProvinceCode, Collections.singletonList(provinceCode));
    }

    public static FieldResult setCityCode(String cityCode) {
        return new FieldResult(CityCode, Collections.singletonList(cityCode));
    }

    public static FieldResult setCountyCode(String countyCode) {
        return new FieldResult(CountyCode, Collections.singletonList(countyCode));
    }

    public static FieldResult setAssistCalculateSummaryId(Long assistCalculateSummaryId) {
        return new FieldResult(AssistCalculateSummaryId, Collections.singletonList(assistCalculateSummaryId));
    }
}