package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;

@Data
public class ListAccountBookVo {
    /**
     * id
     */
    private Integer id;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 增值税种类
     */
    private String valueAddedTaxCate;

    /**
     * 会计准则
     */
    private String accountingStandard;

    /**
     * 启用时间
     */
    private Date startTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否启用凭证审核
     */
    private Boolean enableVoucherVerify;

    /**
     * 是否启用
     */
    private Boolean disable;
}
