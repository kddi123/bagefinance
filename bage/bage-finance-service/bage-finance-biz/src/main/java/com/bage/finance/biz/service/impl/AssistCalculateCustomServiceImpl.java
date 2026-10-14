package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.AssistCalculateCustom;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateAssistCalculateCustomForm;
import com.bage.finance.biz.dto.form.UpdateAssistCalculateCustomForm;
import com.bage.finance.biz.dto.vo.GetAssistCalculateCustomVo;
import com.bage.finance.biz.dto.vo.ListAssistCalculateCustomVo;
import com.bage.finance.biz.enums.AssistCalculateCateCodeEnum;
import com.bage.finance.biz.mapper.AssistCalculateCustomMapper;
import com.bage.finance.biz.service.AssistCalculateCateService;
import com.bage.finance.biz.service.AssistCalculateHandleService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

import static com.bage.finance.biz.domain.AssistCalculateCustomField.*;

/**
 * 辅助核算
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssistCalculateCustomServiceImpl implements AssistCalculateHandleService {
    final AssistCalculateCustomMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;

    @Override
    public AssistCalculateCateCodeEnum getAssistCalculateCateCodeEnum() {
        return AssistCalculateCateCodeEnum.CUSTOM;
    }

    /**
     * 添加自定义辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean create(Object obj) {
        CreateAssistCalculateCustomForm form = (CreateAssistCalculateCustomForm) obj;
        AssistCalculateCustom assistCalculate = objectConvertor.toAssistCalculateCustom(form);
        assistCalculate.initDefault();
        assistCalculate.setMemberId(tokenService.getThreadLocalUserId());
        assistCalculate.setUpdateMemberId(tokenService.getThreadLocalUserId());
        assistCalculate.setTenantId(tokenService.getThreadLocalTenantId());
        if (mapper.insert(assistCalculate) == 0) {
            throw new BizException("创建失败");
        }
        return true;
    }

    /**
     * 查询辅助核算列表
     *
     * @return
     */
    @Override
    public List<ListAssistCalculateCustomVo> listByAssistCalculateSummaryIds(List<Long> assistCalculateSummaryIds) {
        MyBatisWrapper<AssistCalculateCustom> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Disable, AssistCalculateSummaryId,
                C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andIn(AssistCalculateSummaryId, assistCalculateSummaryIds);
        List<AssistCalculateCustom> assistCalculates = mapper.list(wrapper);
        return objectConvertor.toListAssistCalculateVo(assistCalculates);
    }

    /**
     * 修改自定义辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean update(Object obj) {
        UpdateAssistCalculateCustomForm form = (UpdateAssistCalculateCustomForm) obj;
        MyBatisWrapper<AssistCalculateCustom> wrapper = new MyBatisWrapper<>();
        wrapper.update(setUpdateTime(new Date()))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .update(setC1(form.getC1()))
                .update(setC2(form.getC2()))
                .update(setC3(form.getC3()))
                .update(setC4(form.getC4()))
                .update(setC5(form.getC5()))
                .update(setC6(form.getC6()))
                .update(setC7(form.getC7()))
                .update(setC8(form.getC8()))
                .update(setC9(form.getC9()))
                .whereBuilder()
                .andEq(setAssistCalculateSummaryId(form.getId()))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()));
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("修改失败");
        }
        return true;
    }

    /**
     * 获取自定义信息
     *
     * @param assistCalculateSummaryId
     * @return
     */
    @Override
    public GetAssistCalculateCustomVo get(long assistCalculateSummaryId) {
        MyBatisWrapper<AssistCalculateCustom> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(AssistCalculateSummaryId, assistCalculateSummaryId);
        AssistCalculateCustom assistCalculateCustom = mapper.get(wrapper);
        return objectConvertor.toGetAssistCalculateCustomVo(assistCalculateCustom);
    }
}
