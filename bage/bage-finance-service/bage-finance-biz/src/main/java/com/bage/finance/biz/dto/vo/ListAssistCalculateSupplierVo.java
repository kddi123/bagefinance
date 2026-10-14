package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class ListAssistCalculateSupplierVo extends BaseAssistCalculateVo {
    /**
     * 供应商类别
     */
    private String supplierCate;

    /**
     * 经营地址
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