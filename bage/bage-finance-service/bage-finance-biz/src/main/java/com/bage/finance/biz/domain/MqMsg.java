package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * mq消息（表：mq_msg）
 *
 * @author bage
 */
public class MqMsg {
    /**
     * 
     */
    private Long id;

    /**
     * 消息topic
     */
    private String mqTopic;

    /**
     * 消息tag
     */
    private String mqTag;

    /**
     * 消息key
     */
    private String mqKey;

    /**
     * 消息类型类名称
     */
    private String msgClassName;

    /**
     * 事件状态[0:未处理 1:处理成功 2:处理失败]
     */
    private Integer msgStatus;

    /**
     * 错误次数
     */
    private Integer errorCount;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 消息唯一编码,请求id，幂等性处理
     */
    private String requestId;

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
     * 租户id
     */
    private Long tenantId;

    /**
     * 数据编号
     */
    private String dataNo;

    /**
     * 消息内容 json格式
     */
    private String msg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMqTopic() {
        return mqTopic;
    }

    public void setMqTopic(String mqTopic) {
        this.mqTopic = mqTopic;
    }

    public String getMqTag() {
        return mqTag;
    }

    public void setMqTag(String mqTag) {
        this.mqTag = mqTag;
    }

    public String getMqKey() {
        return mqKey;
    }

    public void setMqKey(String mqKey) {
        this.mqKey = mqKey;
    }

    public String getMsgClassName() {
        return msgClassName;
    }

    public void setMsgClassName(String msgClassName) {
        this.msgClassName = msgClassName;
    }

    public Integer getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDataNo() {
        return dataNo;
    }

    public void setDataNo(String dataNo) {
        this.dataNo = dataNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void initDefault() {
        if (this.getMsgClassName() == null) {
            this.setMsgClassName("");
        }
        if (this.getMsgStatus() == null) {
            this.setMsgStatus(0);
        }
        if (this.getErrorCount() == null) {
            this.setErrorCount(0);
        }
        if (this.getErrorMsg() == null) {
            this.setErrorMsg("");
        }
        if (this.getRequestId() == null) {
            this.setRequestId("");
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
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
        }
        if (this.getDataNo() == null) {
            this.setDataNo("");
        }
    }

    public void initUpdate() {
    }
}