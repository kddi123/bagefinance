package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 辅助核算（客户）
 */
@Data
public class CreateAssistCalculateCustomerForm extends CreateAssistCalculateForm {
    /**
     * 客户类别
     */
    private String customerCate;

    /**
     * 统一社会信用代码
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

    /**
     * 详细地址
     */
    @Size(max = 200)
    private String address;

    /**
     * 自定义字段5
     */
    @Size(max = 50)
    private String contacts;

    /**
     * 手机
     */
    @Pattern(regexp = "^(\\d{11})?$", message = "手机格式不正确")
    private String phone;
}