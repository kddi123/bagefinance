package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class GetAssistCalculateCustomerVo extends GetAssistCalculateVo {

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

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;
}