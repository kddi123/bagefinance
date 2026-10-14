package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.AssistCalculateSummary;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.BaseAssistCalculateVo;
import com.bage.finance.biz.dto.vo.GetAssistCalculateVo;
import com.bage.finance.biz.dto.vo.ListAssistCalculateSummaryVo;
import com.bage.mybatis.help.PageInfo;

import java.util.List;
import java.util.Set;

public interface AssistCalculateSummaryService {
    /**
     * 添加自定义辅助核算
     *
     * @param form
     * @return
     */
    AssistCalculateSummary create(AssistCalculateSummary form);

    /**
     * 查询辅助核算列表
     *
     * @return
     */
    PageInfo<ListAssistCalculateSummaryVo> list(ListAssistCalculateSummaryForm form);

    /**
     * 查询辅助核算列表
     *
     * @return
     */
    <R extends BaseAssistCalculateVo> PageInfo<R> list(ListAssistCalculateForm form, Class<R> returnType);

    /**
     * 删除辅助核算
     *
     * @param form
     * @return
     */
    boolean del(DelForm form);

    /**
     * 修改辅助核算
     * @param form
     * @param <T>
     * @return
     */
    <T extends UpdateAssistCalculateForm> boolean update(T form);

    /**
     * 禁用启用自定义辅助核算
     *
     * @param form
     * @return
     */
    boolean updateDisable(UpdateDisableForm form);

    /**
     * 通过辅助核算id查询辅助核算列表
     *
     * @param ids
     * @return
     */
    List<AssistCalculateSummary> list(Set<Long> ids, long tenantId);

    /**
     * 添加自定义辅助核算
     *
     * @param form
     * @return
     */
    <T extends CreateAssistCalculateForm> boolean create(T form);

    /**
     * 获取辅助核算详情
     * @param id
     * @param returnType
     * @param <R>
     * @return
     */
    <R extends GetAssistCalculateVo> R get(long id, Class<R> returnType);

    /**
     * 增加辅助核算使用计数
     * @param id
     * @param count
     * @return
     */
    boolean addUseCount(long id,int count);

    /**
     * 增加辅助核算使用计数
     *
     * @param ids
     * @return
     */
    int addUseCount(Set<Long> ids);

    /**
     * 减少科目使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean deductUseCount(long id, int count);

    /**
     * 减少辅助核算使用计数
     * @param ids
     * @return
     */
    int deductUseCount(Set<Long> ids);
}
