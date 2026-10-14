package com.bage.finance.biz.service;


import com.bage.finance.biz.dto.vo.BaseAssistCalculateVo;
import com.bage.finance.biz.enums.AssistCalculateCateCodeEnum;

import java.util.List;

/**
 * 辅助核算接口
 */
public interface AssistCalculateHandleService {
    AssistCalculateCateCodeEnum getAssistCalculateCateCodeEnum();

    /**
     * 添加客户辅助核算
     *
     * @param obj
     * @return
     */
    boolean create(Object obj);

    /**
     * 查询客户辅助核算客户列表
     *
     * @return
     */
    List<? extends BaseAssistCalculateVo> listByAssistCalculateSummaryIds(List<Long> assistCalculateSummaryIds);

    /**
     * 修改客户辅助核算
     *
     * @param form
     * @return
     */
    boolean update(Object form);

    /**
     * 获取客户辅助核算详情
     * @param assistCalculateSummaryId
     * @return
     */
    Object get(long assistCalculateSummaryId);
}
