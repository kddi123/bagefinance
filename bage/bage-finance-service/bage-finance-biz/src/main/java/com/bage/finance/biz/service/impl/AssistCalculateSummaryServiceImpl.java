package com.bage.finance.biz.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.AssistCalculateCate;
import com.bage.finance.biz.domain.AssistCalculateSummary;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.BaseAssistCalculateVo;
import com.bage.finance.biz.dto.vo.GetAssistCalculateVo;
import com.bage.finance.biz.dto.vo.ListAssistCalculateSummaryVo;
import com.bage.finance.biz.factory.AssistCalculateHandlerFactory;
import com.bage.finance.biz.mapper.AssistCalculateSummaryMapper;
import com.bage.finance.biz.service.AssistCalculateCateService;
import com.bage.finance.biz.service.AssistCalculateHandleService;
import com.bage.finance.biz.service.AssistCalculateSummaryService;
import com.bage.mybatis.help.MyBatisWrapper;
import com.bage.mybatis.help.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.AssistCalculateSummaryField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistCalculateSummaryServiceImpl implements AssistCalculateSummaryService {
    final AssistCalculateSummaryMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final AssistCalculateCateService assistCalculateCateService;
    final TransactionTemplate transactionTemplate;
    final AssistCalculateHandlerFactory assistCalculateHandlerFactory;


    /**
     * 添加客户辅助核算
     *
     * @param form
     * @return
     */
    @Override
    public AssistCalculateSummary create(AssistCalculateSummary form) {
        form.initDefault();
        form.setMemberId(tokenService.getThreadLocalUserId());
        form.setUpdateMemberId(tokenService.getThreadLocalUserId());
        form.setTenantId(tokenService.getThreadLocalTenantId());
        form.setMnemonicCode(PinyinUtil.getFirstLetter(form.getName(), ""));
        mapper.insert(form);
        return form;
    }

    /**
     * 查询客户辅助核算客户列表
     *
     * @return
     */
    @Override
    public PageInfo<ListAssistCalculateSummaryVo> list(ListAssistCalculateSummaryForm form) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Code, MnemonicCode, Disable)
                .page(form.getPageNum(), form.getPageSize())
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        if (form.getAssistCalculateCateId() != null) {
            wrapper.and()
                    .andEq(AssistCalculateCateId, form.getAssistCalculateCateId());
        }
        if (Strings.isNotBlank(form.getCodeOrName())) {
            wrapper.and()
                    .andEq(Code, form.getCodeOrName())
                    .orLike(Name, "%" + form.getCodeOrName() + "%");
        }
        PageInfo<AssistCalculateSummary> assistCalculates = wrapper.listPage(mapper);
        return objectConvertor.toListAssistCalculateSummaryVo(assistCalculates);
    }

    /**
     * 查询辅助核算列表
     *
     * @return
     */
    @Override
    public <R extends BaseAssistCalculateVo> PageInfo<R> list(ListAssistCalculateForm form, Class<R> returnType) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Code, MnemonicCode, Disable, AssistCalculateCateCode, Notes)
                .page(form.getPageNum(), form.getPageSize())
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(AssistCalculateCateId, form.getAssistCalculateCateId());
        if (Strings.isNotBlank(form.getCodeOrName())) {
            wrapper.and()
                    .andEq(Code, form.getCodeOrName())
                    .orLike(Name, "%" + form.getCodeOrName() + "%");
        }
        PageInfo<AssistCalculateSummary> assistCalculateSummaryPageInfo = wrapper.listPage(mapper);
        if (CollectionUtils.isEmpty(assistCalculateSummaryPageInfo.getList())) {
            return new PageInfo<>();
        }
        String cateCode = assistCalculateSummaryPageInfo.getList().get(0).getAssistCalculateCateCode();
        List<Long> ids = assistCalculateSummaryPageInfo.getList().stream().map(AssistCalculateSummary::getId)
                .collect(Collectors.toList());
        AssistCalculateHandleService assistCalculateHandleService = assistCalculateHandlerFactory.get(cateCode);
        List<? extends BaseAssistCalculateVo> customList =
                assistCalculateHandleService.listByAssistCalculateSummaryIds(ids);
        if (CollectionUtils.isEmpty(customList)) {
            throw new BizException("数据异常");
        }
        List<R> data = convertToList(customList, returnType);
        data.forEach(item -> {
            assistCalculateSummaryPageInfo.getList().stream().filter(p -> p.getId().equals(item.getAssistCalculateSummaryId()))
                    .findFirst().ifPresent(i -> {
                item.setId(i.getId());
                item.setCode(i.getCode());
                item.setName(i.getName());
                item.setNotes(i.getNotes());
                item.setMnemonicCode(i.getMnemonicCode());
                item.setDisable(i.getDisable());
            });
        });
        PageInfo<R> result = new PageInfo<>();
        result.setPageNum(assistCalculateSummaryPageInfo.getPageNum());
        result.setPageSize(assistCalculateSummaryPageInfo.getPageSize());
        result.setPages(assistCalculateSummaryPageInfo.getPages());
        result.setTotal(assistCalculateSummaryPageInfo.getTotal());
        result.setList(data);
        return result;
    }

    // 辅助方法，用于将 AssistCalculateCustom 对象转换为指定的类型 R
    private <R extends BaseAssistCalculateVo> List<R> convertToList(List<? extends BaseAssistCalculateVo> customs, Class<R> returnType) {
        List<R> result = new ArrayList<>();
        for (Object custom : customs) {
            // 进行转换并添加到结果列表中
            result.add(returnType.cast(custom));
        }
        return result;
    }

    /**
     * 删除客户辅助核算
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelForm form) {
        AssistCalculateSummary assistCalculateCustom = getById(form.getId());
        if (assistCalculateCustom == null) {
            throw new BizException("自定义辅助核算不存在");
        }

        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.updateConcat(setCode("_del_" + form.getId()))
                .update(setDelFlag(true))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .update(setUpdateTime(new Date()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));

        transactionTemplate.execute((transactionStatus) -> {
            if (mapper.updateField(wrapper) == 0) {
                throw new BizException("删除失败");
            }
            if (!assistCalculateCateService.deductUseCount(assistCalculateCustom.getAssistCalculateCateId(), 1)) {
                throw new BizException("删除失败");
            }
            return true;
        });
        return true;
    }

    /**
     * 修改客户辅助核算
     *
     * @param form
     * @return
     */
    @Override
    public <T extends UpdateAssistCalculateForm> boolean update(T form) {
        AssistCalculateSummary assistCalculate = getById(form.getId());
        if (assistCalculate == null) {
            throw new BizException("辅助核算不存在");
        }
        AssistCalculateSummary assistCalculate2 = getByAssistCalculateCateIdAndCode(assistCalculate.getAssistCalculateCateId(),
                form.getCode());
        if (assistCalculate2 != null
                && !assistCalculate2.getId().equals(form.getId())) {
            throw new BizException("辅助核算编码已存在");
        }
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.update(Code, form.getCode())
                .update(Name, form.getName())
                .update(MnemonicCode, PinyinUtil.getFirstLetter(form.getName(), ""))
                .update(UpdateTime, new Date())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(Notes, form.getNotes())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        transactionTemplate.execute((transactionStatus) -> {
            if (mapper.updateField(wrapper) == 0) {
                throw new BizException("修改失败");
            }
            //如果是自定义辅助核算
            AssistCalculateHandleService assistCalculateHandleService = assistCalculateHandlerFactory.get(assistCalculate.getAssistCalculateCateCode());
            if (!assistCalculateHandleService.update(form)) {
                throw new BizException("修改失败");
            }

            return true;
        });
        return true;
    }

    /**
     * 禁用启用辅助核算
     *
     * @param form
     * @return
     */
    @Override
    public boolean updateDisable(UpdateDisableForm form) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.update(Disable, form.getDisable())
                .update(UpdateTime, DateUtil.getSystemTime())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(Disable, !form.getDisable());
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 通过辅助核算id查询辅助核算列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<AssistCalculateSummary> list(Set<Long> ids, long tenantId) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Code, MnemonicCode, Disable, AssistCalculateCateId)
                .whereBuilder()
                .andEq(TenantId, tenantId)
                .andIn(Id, ids)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 添加自定义辅助核算
     *
     * @param form
     * @return
     */
    @Override
    public <T extends CreateAssistCalculateForm> boolean create(T form) {
        AssistCalculateSummary assistCalculateSummary = objectConvertor.toAssistCalculateSummary(form);
        //创建前检查
        createCheck(assistCalculateSummary);
        transactionTemplate.execute((transactionStatus) -> {
            create(assistCalculateSummary);
            form.setAssistCalculateSummaryId(assistCalculateSummary.getId());
            AssistCalculateHandleService assistCalculateHandleService = assistCalculateHandlerFactory
                    .get(assistCalculateSummary.getAssistCalculateCateCode());
            assistCalculateHandleService.create(form);
            if (!assistCalculateCateService.addUseCount(form.getAssistCalculateCateId(), 1)) {
                throw new BizException("创建失败");
            }
            return true;
        });
        return true;
    }

    /**
     * 获取辅助核算详情
     *
     * @param id
     * @param returnType
     * @param <R>
     * @return
     */
    @Override
    public <R extends GetAssistCalculateVo> R get(long id, Class<R> returnType) {
        AssistCalculateSummary assistCalculateSummary = getById(id);
        if (assistCalculateSummary == null) {
            throw new BizException("辅助核算不存在");
        }
        AssistCalculateHandleService assistCalculateHandleService = assistCalculateHandlerFactory.get(assistCalculateSummary.getAssistCalculateCateCode());
        R result = returnType.cast(assistCalculateHandleService.get(id));
        if (result == null) {
            throw new BizException("记录不存在");
        }
        result.setId(assistCalculateSummary.getId());
        result.setCode(assistCalculateSummary.getCode());
        result.setName(assistCalculateSummary.getName());
        result.setNotes(assistCalculateSummary.getNotes());
        result.setMnemonicCode(assistCalculateSummary.getMnemonicCode());

        return result;
    }

    /**
     * 增加科目使用计数
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public boolean addUseCount(long id, int count) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(UseCount, count)
                .whereBuilder().andEq(Id, id);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("增加辅助核算使用计数异常");
        }
        return true;
    }

    /**
     * 增加科目使用计数
     *
     * @param ids
     * @return
     */
    @Override
    public int addUseCount(Set<Long> ids) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(setUseCount(1))
                .whereBuilder().andIn(Id, ids);
        return mapper.updateField(wrapper);
    }

    /**
     * 减少科目使用计数
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public boolean deductUseCount(long id, int count) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(setUseCount(count));
        wrapper.whereBuilder()
                .andEq(setId(id))
                .andGTE(setUseCount(count));
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("减少辅助核算类别使用计数异常");
        }
        return true;
    }

    /**
     * 减少科目使用计数
     *
     * @param ids
     * @return
     */
    @Override
    public int deductUseCount(Set<Long> ids) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(setUseCount(1));
        wrapper.whereBuilder()
                .andIn(Id, ids)
                .andGT(setUseCount(0));
        return mapper.updateField(wrapper);
    }

    /**
     * 查询辅助核算类别信息
     *
     * @param assistCalculateCateId
     * @param code
     * @return
     */
    private AssistCalculateSummary getByAssistCalculateCateIdAndCode(long assistCalculateCateId, String code) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, Name)
                .whereBuilder()
                .andEq(setCode(code))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false))
                .andEq(setAssistCalculateCateId(assistCalculateCateId));
        return mapper.get(wrapper);
    }

    /**
     * 创建辅助核算前校验
     *
     * @param form
     */
    private void createCheck(AssistCalculateSummary form) {
        AssistCalculateCate assistCalculateCate = assistCalculateCateService.getById2(form.getAssistCalculateCateId());
        if (assistCalculateCate == null) {
            throw new BizException("辅助核算类别编码不存在");
        }
        AssistCalculateSummary assistCalculateSummary = getByAssistCalculateCateIdAndCode(form.getAssistCalculateCateId(),
                form.getCode());
        if (assistCalculateSummary != null
                && !assistCalculateSummary.getId().equals(form.getId())) {
            throw new BizException("辅助核算编码已存在");
        }
        form.setAssistCalculateCateCode(assistCalculateCate.getCode());
    }

    /**
     * 查询辅助核算明细
     *
     * @param id
     * @return
     */
    private AssistCalculateSummary getById(long id) {
        MyBatisWrapper<AssistCalculateSummary> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Code, Name, AssistCalculateCateId, AssistCalculateCateCode, Notes)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.get(wrapper);
    }

}
