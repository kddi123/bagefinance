package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 辅助核算客户列表
 */
@Data
public class ListAssistCalculateCustomerVo extends BaseAssistCalculateVo {
    /**
     * 客户类别
     */
    private String customerCate;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 手机
     */
    private String phone;

    /**
     * 税号
     */
    private String unifiedSocialCreditCode;
}