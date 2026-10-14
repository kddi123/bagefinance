package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class MqMsgField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField MqTopic = new DbField("mq_topic","mqTopic","VARCHAR","java.lang.String");

    public static DbField MqTag = new DbField("mq_tag","mqTag","VARCHAR","java.lang.String");

    public static DbField MqKey = new DbField("mq_key","mqKey","VARCHAR","java.lang.String");

    public static DbField MsgClassName = new DbField("msg_class_name","msgClassName","VARCHAR","java.lang.String");

    public static DbField MsgStatus = new DbField("msg_status","msgStatus","TINYINT","java.lang.Integer");

    public static DbField ErrorCount = new DbField("error_count","errorCount","INTEGER","java.lang.Integer");

    public static DbField ErrorMsg = new DbField("error_msg","errorMsg","VARCHAR","java.lang.String");

    public static DbField RequestId = new DbField("request_id","requestId","VARCHAR","java.lang.String");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField DataNo = new DbField("data_no","dataNo","VARCHAR","java.lang.String");

    public static DbField Msg = new DbField("msg","msg","LONGVARCHAR","java.lang.String");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setMqTopic(String mqTopic) {
        return new FieldResult(MqTopic, Collections.singletonList(mqTopic));
    }

    public static FieldResult setMqTag(String mqTag) {
        return new FieldResult(MqTag, Collections.singletonList(mqTag));
    }

    public static FieldResult setMqKey(String mqKey) {
        return new FieldResult(MqKey, Collections.singletonList(mqKey));
    }

    public static FieldResult setMsgClassName(String msgClassName) {
        return new FieldResult(MsgClassName, Collections.singletonList(msgClassName));
    }

    public static FieldResult setMsgStatus(Integer msgStatus) {
        return new FieldResult(MsgStatus, Collections.singletonList(msgStatus));
    }

    public static FieldResult setErrorCount(Integer errorCount) {
        return new FieldResult(ErrorCount, Collections.singletonList(errorCount));
    }

    public static FieldResult setErrorMsg(String errorMsg) {
        return new FieldResult(ErrorMsg, Collections.singletonList(errorMsg));
    }

    public static FieldResult setRequestId(String requestId) {
        return new FieldResult(RequestId, Collections.singletonList(requestId));
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

    public static FieldResult setDataNo(String dataNo) {
        return new FieldResult(DataNo, Collections.singletonList(dataNo));
    }

    public static FieldResult setMsg(String msg) {
        return new FieldResult(Msg, Collections.singletonList(msg));
    }
}