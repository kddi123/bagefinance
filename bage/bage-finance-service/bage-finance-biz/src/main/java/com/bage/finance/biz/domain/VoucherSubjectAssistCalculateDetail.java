package com.bage.finance.biz.domain;

import java.util.Date;

/**
 * 凭证辅科目辅助核算明细表（表：voucher_subject_assist_calculate_detail）
 *
 * @author bage
 */
public class VoucherSubjectAssistCalculateDetail {
    /**
     * 
     */
    private Long id;

    /**
     * 凭证id
     */
    private Long voucherId;

    /**
     * 科目id
     */
    private Long subjectId;

    /**
     * 凭证科目明细id
     */
    private Long voucherSubjectDetailId;

    /**
     * 辅助核算类别id
     */
    private Long assistCalculateCateId;

    /**
     * 辅助核算类别编码
     */
    private String assistCalculateCateCode;

    /**
     * 辅助核算id
     */
    private Long assistCalculateId;

    /**
     * 辅助核算编码
     */
    private String assistCalculateCode;

    /**
     * 辅助核算名称
     */
    private Long assistCalculateName;

    /**
     * 租户id
     */
    private Long tenantId;

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
     * 第几行数据，行编号
     */
    private Integer rowNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getVoucherSubjectDetailId() {
        return voucherSubjectDetailId;
    }

    public void setVoucherSubjectDetailId(Long voucherSubjectDetailId) {
        this.voucherSubjectDetailId = voucherSubjectDetailId;
    }

    public Long getAssistCalculateCateId() {
        return assistCalculateCateId;
    }

    public void setAssistCalculateCateId(Long assistCalculateCateId) {
        this.assistCalculateCateId = assistCalculateCateId;
    }

    public String getAssistCalculateCateCode() {
        return assistCalculateCateCode;
    }

    public void setAssistCalculateCateCode(String assistCalculateCateCode) {
        this.assistCalculateCateCode = assistCalculateCateCode;
    }

    public Long getAssistCalculateId() {
        return assistCalculateId;
    }

    public void setAssistCalculateId(Long assistCalculateId) {
        this.assistCalculateId = assistCalculateId;
    }

    public String getAssistCalculateCode() {
        return assistCalculateCode;
    }

    public void setAssistCalculateCode(String assistCalculateCode) {
        this.assistCalculateCode = assistCalculateCode;
    }

    public Long getAssistCalculateName() {
        return assistCalculateName;
    }

    public void setAssistCalculateName(Long assistCalculateName) {
        this.assistCalculateName = assistCalculateName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public Integer getRowNo() {
        return rowNo;
    }

    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }

    public void initDefault() {
        if (this.getVoucherId() == null) {
            this.setVoucherId(0L);
        }
        if (this.getSubjectId() == null) {
            this.setSubjectId(0L);
        }
        if (this.getVoucherSubjectDetailId() == null) {
            this.setVoucherSubjectDetailId(0L);
        }
        if (this.getAssistCalculateCateId() == null) {
            this.setAssistCalculateCateId(0L);
        }
        if (this.getAssistCalculateCateCode() == null) {
            this.setAssistCalculateCateCode("");
        }
        if (this.getAssistCalculateId() == null) {
            this.setAssistCalculateId(0L);
        }
        if (this.getAssistCalculateCode() == null) {
            this.setAssistCalculateCode("");
        }
        if (this.getAssistCalculateName() == null) {
            this.setAssistCalculateName(0L);
        }
        if (this.getTenantId() == null) {
            this.setTenantId(0L);
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
        if (this.getRowNo() == null) {
            this.setRowNo(0);
        }
    }

    public void initUpdate() {
    }
}