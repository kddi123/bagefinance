package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 辅助核算现金流列表
 */
@Data
public class ListAssistCalculateCashFlowVo extends BaseAssistCalculateVo{
    /**
     *现金流类别
     */
    private String cashFlowCate;
}