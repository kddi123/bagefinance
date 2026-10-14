package com.bage.finance.biz.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 凭证表（表：voucher）
 *
 * @author bage
 */
public class Voucher {
    /**
     * 
     */
    private Long id;

    /**
     * 凭证字配置id
     */
    private Long voucherWordConfigId;

    /**
     * 凭证号
     */
    private Integer voucherNumber;

    /**
     * 凭证日期
     */
    private Date voucherDate;

    /**
     * 单据数量
     */
    private Integer documentNum;

    /**
     * 凭证总金额
     */
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    private String notes;

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
     * 凭证版本
     */
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoucherWordConfigId() {
        return voucherWordConfigId;
    }

    public void setVoucherWordConfigId(Long voucherWordConfigId) {
        this.voucherWordConfigId = voucherWordConfigId;
    }

    public Integer getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(Integer voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public Date getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    public Integer getDocumentNum() {
        return documentNum;
    }

    public void setDocumentNum(Integer documentNum) {
        this.documentNum = documentNum;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void initDefault() {
        if (this.getVoucherWordConfigId() == null) {
            this.setVoucherWordConfigId(0L);
        }
        if (this.getVoucherNumber() == null) {
            this.setVoucherNumber(0);
        }
        if (this.getVoucherDate() == null) {
            this.setVoucherDate(new Date());
        }
        if (this.getDocumentNum() == null) {
            this.setDocumentNum(0);
        }
        if (this.getTotalAmount() == null) {
            this.setTotalAmount(new BigDecimal("0.00"));
        }
        if (this.getNotes() == null) {
            this.setNotes("");
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
        if (this.getVersion() == null) {
            this.setVersion(0);
        }
    }

    public void initUpdate() {
    }
}