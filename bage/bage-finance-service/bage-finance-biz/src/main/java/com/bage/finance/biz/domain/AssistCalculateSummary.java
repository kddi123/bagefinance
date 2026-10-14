package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 辅助核算汇总表（所有类别）（表：assist_calculate_summary）
 *
 * @author bage
 */
public class AssistCalculateSummary {
    /**
     * 
     */
    private Long id;

    /**
     * 辅助核算名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

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
     * 助记码
     */
    private String mnemonicCode;

    /**
     * 辅助核算类别id
     */
    private Long assistCalculateCateId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 辅助核算类别编码
     */
    private String assistCalculateCateCode;

    /**
     * 
     */
    private String notes;

    /**
     * 使用计数
     */
    private Integer useCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public Long getAssistCalculateCateId() {
        return assistCalculateCateId;
    }

    public void setAssistCalculateCateId(Long assistCalculateCateId) {
        this.assistCalculateCateId = assistCalculateCateId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAssistCalculateCateCode() {
        return assistCalculateCateCode;
    }

    public void setAssistCalculateCateCode(String assistCalculateCateCode) {
        this.assistCalculateCateCode = assistCalculateCateCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public void initDefault() {
        if (this.getName() == null) {
            this.setName("");
        }
        if (this.getCode() == null) {
            this.setCode("");
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
        if (this.getMnemonicCode() == null) {
            this.setMnemonicCode("");
        }
        if (this.getAssistCalculateCateId() == null) {
            this.setAssistCalculateCateId(0L);
        }
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
        }
        if (this.getAssistCalculateCateCode() == null) {
            this.setAssistCalculateCateCode("");
        }
        if (this.getNotes() == null) {
            this.setNotes("");
        }
        if (this.getUseCount() == null) {
            this.setUseCount(0);
        }
    }

    public void initUpdate() {
    }
}