package com.bage.finance.biz.dto.form;

import com.bage.common.dto.PageHelperRequest;

import lombok.Data;

@Data
public class ListAccountBookForm extends PageHelperRequest {
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 是否禁用；true：禁用；false：启用
     */
    private Boolean disable;
}
