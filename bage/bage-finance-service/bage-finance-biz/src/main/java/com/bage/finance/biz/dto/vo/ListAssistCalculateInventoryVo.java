package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;

/**
 * 辅助核算存货列表
 */
@Data
public class ListAssistCalculateInventoryVo extends BaseAssistCalculateVo {
    /**
     * 规格型号
     */
    private String specifications;

    /**
     * 存货类别
     */
    private String inventoryCate;

    /**
     * 计量单位
     */
    private String units;

    /**
     * 启用日期
     */
    private Date startDate;

    /**
     * 停用日期
     */
    private Date endDate;
}