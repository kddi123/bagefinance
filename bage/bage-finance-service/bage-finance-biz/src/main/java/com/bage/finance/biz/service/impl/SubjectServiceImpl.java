package com.bage.finance.biz.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.excel.EasyExcel;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.common.util.MyUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.*;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.*;
import com.bage.finance.biz.enums.BalanceDirectionEnum;
import com.bage.finance.biz.enums.SubjectCateEnum;
import com.bage.finance.biz.enums.SubjectTypeEnum;
import com.bage.finance.biz.mapper.SubjectMapper;
import com.bage.finance.biz.service.*;
import com.bage.mybatis.help.Criteria;
import com.bage.mybatis.help.FieldResult;
import com.bage.mybatis.help.MyBatisWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.SubjectField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    final SubjectMapper mapper;
    final CurrencyConfigService currencyConfigService;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final TransactionTemplate transactionTemplate;
    final ObjectMapper jsonMapper;
    final TenantSysConfigService tenantSysConfigService;
    final AssistCalculateCateService assistCalculateCateService;
    final RedissonClient redissonClient;
    final AssistCalculateSummaryService assistCalculateSummaryService;

    /**
     * 创建科目
     *
     * @param form
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public boolean create(CreateSubjectForm form) throws JsonProcessingException {
        // 获取科目类别锁
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SUBJECT_LOCK + tokenService.getThreadLocalTenantId());
        try {
            rLock.lock();
            Subject getSubject = getByCode(form.getCode());
            if (getSubject != null) {
                throw new BizException("科目编码已存在");
            }
            Subject subject = objectConvertor.toSubject(form);
            subject.setNodeDepth(0);
            Subject parentSubject = getById(subject.getPid());
            if (parentSubject == null) {
                throw new BizException("上级科目不存在");
            }
            if (parentSubject.getDisable()) {
                throw new BizException("上级科目已停用");
            }
            if (parentSubject.getNodeDepth() >= 3) {
                throw new BizException("最多只能创建4级下级科目");
            }
            subject.setSubjectCate(parentSubject.getSubjectCate());
            //检查科目编码格式
            SubjectCalculateConfigVo parentSubjectCalculateConfigVo = checkSubjectCode(parentSubject, subject);

            //如果上级科目启用了数量核算
            if (parentSubjectCalculateConfigVo.getEnableNumberCalculateConfig()) {
                if (!form.getSubjectCalculateConfigForm().getEnableNumberCalculateConfig()) {
                    throw new BizException("数量核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentNumberCalculateConfigFlag(true);
                form.getSubjectCalculateConfigForm()
                        .setNumberCalculateConfig(
                                objectConvertor.toNumberCalculateConfig(parentSubjectCalculateConfigVo.getNumberCalculateConfig()));
            }
            //如果科目启用了数量核算
            if (form.getSubjectCalculateConfigForm().getEnableNumberCalculateConfig()
                    && (form.getSubjectCalculateConfigForm().getNumberCalculateConfig() == null
                    || Strings.isBlank(form.getSubjectCalculateConfigForm().getNumberCalculateConfig().getUnitOfMeasurement())
            )) {
                throw new BizException("请设置数量核算单位名称！");
            }

            //如果上级科目启用了辅助核算
            if (parentSubjectCalculateConfigVo.getEnableAssistCalculateConfigs()) {
                if (!form.getSubjectCalculateConfigForm().getEnableAssistCalculateConfigs()) {
                    throw new BizException("辅助核算配置非法");
                }
                if (CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getAssistCalculateConfigs())) {
                    throw new BizException("辅助核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentAssistCalculateConfigsFlag(true);
                //获取上级科目辅助核算配置
                List<SubjectCalculateConfigForm.AssistCalculateConfig> parentAssistCalculateConfigList = objectConvertor
                        .toAssistCalculateConfig(parentSubjectCalculateConfigVo.getAssistCalculateConfigs());
                for (SubjectCalculateConfigForm.AssistCalculateConfig assistCalculateConfig : parentAssistCalculateConfigList) {
                    //获取前端传过来的辅助核算类别id在上级科目所设置的辅助核算是否能找到
                    SubjectCalculateConfigForm.AssistCalculateConfig formAssistCalculateConfig = form.getSubjectCalculateConfigForm()
                            .getAssistCalculateConfigs().stream()
                            .filter(p -> p.getAssistCalculateId().equals(assistCalculateConfig.getAssistCalculateId()))
                            .findFirst().orElseThrow(() -> new BizException("辅助核算配置非法"));
                    //辅助核算类别是否必填，只能通过前端传递不能继承上级科目所设置的辅助核算必填项
                    assistCalculateConfig.setRequiredFlag(formAssistCalculateConfig.getRequiredFlag());
                }
                form.getSubjectCalculateConfigForm().setAssistCalculateConfigs(parentAssistCalculateConfigList);
            }
            //如果启用了辅助核算配置
            Set<Long> assistCalculateCateIds = null;
            if (form.getSubjectCalculateConfigForm().getEnableAssistCalculateConfigs()) {
                if (CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getAssistCalculateConfigs())) {
                    throw new BizException("请设置辅助核算的具体核算项！");
                }
                assistCalculateCateIds = form.getSubjectCalculateConfigForm().getAssistCalculateConfigs().stream()
                        .map(SubjectCalculateConfigForm.AssistCalculateConfig::getAssistCalculateId).collect(Collectors.toSet());
                List<AssistCalculateCate> assistCalculateCates = assistCalculateCateService.list(assistCalculateCateIds);
                if (assistCalculateCateIds.size() != assistCalculateCates.size()) {
                    throw new BizException("辅助核算参数非法");
                }
            }

            //如果上级科目启用了外币核算
            if (parentSubjectCalculateConfigVo.getEnableForeignCurrencyConfig()) {
                if (!form.getSubjectCalculateConfigForm().getEnableForeignCurrencyConfig()) {
                    throw new BizException("外币核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentForeignCurrencyConfigFlag(true);
                if (parentSubjectCalculateConfigVo.getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()
                        && !form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()) {
                    throw new BizException("外币核算配置非法");
                }
                Set<Long> parentSubjectCurrencyConfigIds = parentSubjectCalculateConfigVo.getForeignCurrencyConfig().getCurrencyConfigIds();
                form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().setParentSubjectCurrencyConfigIds(parentSubjectCurrencyConfigIds);
                for (Long parentSubjectCurrencyConfigId : parentSubjectCurrencyConfigIds) {
                    form.getSubjectCalculateConfigForm()
                            .getForeignCurrencyConfig().getCurrencyConfigIds().stream().filter(p -> p.equals(parentSubjectCurrencyConfigId))
                            .findFirst().orElseThrow(() -> new BizException("外币核算配置非法"));
                }
            }
            //如果启用了外币核算
            if (form.getSubjectCalculateConfigForm().getEnableForeignCurrencyConfig()) {
                if (form.getSubjectCalculateConfigForm().getForeignCurrencyConfig() == null
                        || CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds())) {
                    throw new BizException("请设置外币核算的具体核算项！");
                }
                List<CurrencyConfig> currencyConfigs = currencyConfigService.list(form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds());
                if (currencyConfigs.size() != form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds().size()) {
                    throw new BizException("外币核算参数非法");
                }
                if (currencyConfigs.size() == 1 && currencyConfigs.get(0).getBaseCurrencyFlag()) {
                    throw new BizException("不涉及外币，无需设置外币核算");
                }
            } else if (!form.getSubjectCalculateConfigForm().getEnableForeignCurrencyConfig()
                    && form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()) {
                throw new BizException("只要启用了外币核算才能启用期末调汇");
            }

            //设置父节点全路径
            if (parentSubject.getPid() == 0) {
                subject.setPidPath("/" + parentSubject.getId() + "/");
            } else {
                subject.setPidPath(parentSubject.getPidPath() + parentSubject.getId() + "/");
            }
            subject.setCalculateConfig(jsonMapper.writeValueAsString(form.getSubjectCalculateConfigForm()));
            subject.setMemberId(tokenService.getThreadLocalUserId());
            subject.setUpdateMemberId(subject.getMemberId());
            subject.setTenantId(tokenService.getThreadLocalTenantId());
            subject.setSubjectType(SubjectTypeEnum.CUSTOM.getCode());
            subject.setParentSubjectDisable(parentSubject.getParentSubjectDisable());
            subject.setMnemonicCode(parentSubject.getMnemonicCode() + "_" + PinyinUtil.getFirstLetter(subject.getName(), ""));
            subject.initDefault();
            Set<Long> finalAssistCalculateCateIds = assistCalculateCateIds;
            transactionTemplate.execute((transactionStatus) -> {
                mapper.insert(subject);
                if (!addUseCount(subject.getPid())) {
                    throw new BizException("创建科目失败");
                }
                if (!CollectionUtils.isEmpty(finalAssistCalculateCateIds)) {
                    //增加辅助核算类别引用计数
                    assistCalculateCateService.addUseCount(finalAssistCalculateCateIds);
                }
                //增加币别使用计数
                if (form.getSubjectCalculateConfigForm() != null &&
                        form.getSubjectCalculateConfigForm().getForeignCurrencyConfig() != null
                        && !CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm()
                        .getForeignCurrencyConfig().getCurrencyConfigIds())) {
                    currencyConfigService.addUseCount(form.getSubjectCalculateConfigForm()
                            .getForeignCurrencyConfig().getCurrencyConfigIds());
                }
                return true;
            });
            return true;
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("创建科目失败", ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 删除科目
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelForm form) throws JsonProcessingException {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.updateConcat(setCode("_del_" + form.getId()))
                .update(setDelFlag(true))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .update(setUpdateTime(new Date()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false))
                .andEq(setSubjectType(SubjectTypeEnum.CUSTOM.getCode()))
                //未使用的才能删除
                .andEq(setUseCount(0));
        // 获取科目类别锁
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SUBJECT_LOCK + tokenService.getThreadLocalTenantId());
        try {
            rLock.lock();
            Subject subject = getById(form.getId());
            if (subject == null) {
                throw new BizException("科目不存在");
            }
            SubjectCalculateConfig subjectCalculateConfig = jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfig.class);
            transactionTemplate.execute((transactionStatus) -> {
                if (mapper.updateField(wrapper) == 0) {
                    throw new BizException("删除失败");
                }

                //扣减上级科目引用计数
                if (subject.getPid() > 0 && !deductUseCount(subject.getPid(), 1)) {
                    throw new BizException("删除失败");
                }
                //扣减辅助核算类别引用计数
                if (subjectCalculateConfig != null
                        && !CollectionUtils.isEmpty(subjectCalculateConfig.getAssistCalculateConfigs())) {
                    Set<Long> assistCalculateCateIds = subjectCalculateConfig.getAssistCalculateConfigs().stream()
                            .map(SubjectCalculateConfig.AssistCalculateConfig::getAssistCalculateId)
                            .collect(Collectors.toSet());
                    assistCalculateCateService.deductUseCount(assistCalculateCateIds);
                }
                if (subjectCalculateConfig != null
                        && subjectCalculateConfig.getForeignCurrencyConfig() != null
                        && !CollectionUtils.isEmpty(subjectCalculateConfig.getForeignCurrencyConfig().getCurrencyConfigIds())) {
                    currencyConfigService.decrUseCount(subjectCalculateConfig.getForeignCurrencyConfig().getCurrencyConfigIds());
                }
                return true;
            });
            return true;
        } catch (Exception ex) {
            throw new BizException("删除科目失败", ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 禁用或启用科目
     *
     * @param form
     * @return
     */
    @Override
    public boolean disable(DisableSubjectForm form) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.update(setDisable(form.getDisable()))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .update(setUpdateTime(new Date()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDisable(!form.getDisable()));
//        if (form.getDisable()) {
//            //未使用的才能禁用
//            wrapper.and().andEq(setUseCount(0));
//        }
        // 获取科目类别锁
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SUBJECT_LOCK + tokenService.getThreadLocalTenantId());
        try {
            rLock.lock();
            Subject subject = getById(form.getId());
            if (subject == null) {
                throw new BizException("科目不存在");
            }
            if (subject.getPid() > 0) {
                Subject parentSubject = getById(subject.getPid());
                if (parentSubject == null) {
                    throw new BizException("上级科目不存在");
                }
                //如果存在上级科目
                if (parentSubject.getDisable() && !form.getDisable()) {
                    throw new BizException("当前科目不能启用，上级科目已禁用");
                }
            }
            int childCount = getChildCount(subject.getId(), subject.getPidPath());
            //获取一级科目子科目数量
            int oneChildCount = getChildOneCount(form.getId());
            transactionTemplate.execute((transactionStatus) -> {
                if (mapper.updateField(wrapper) == 0) {
                    throw new BizException("禁用或启用失败");
                }
                //禁用
                if (childCount > 0 && form.getDisable()) {
                    int updateRows = updateChildDisable(subject.getId(), subject.getPidPath());
                    if (childCount != updateRows) {
                        throw new BizException("更新失败");
                    }
                }
                //启用
                if (oneChildCount > 0 && !form.getDisable()) {
                    int updateRows = updateParentSubjectDisable(form.getId());
                    if (oneChildCount != updateRows) {
                        throw new BizException("更新失败");
                    }
                }
                return true;
            });
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("禁用或启用异常", ex);
        } finally {
            rLock.unlock();
        }

        return true;
    }

    /**
     * 修改科目
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateSubjectForm form) throws JsonProcessingException {
        // 获取科目类别锁
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SUBJECT_LOCK + tokenService.getThreadLocalTenantId());
        try {
            rLock.lock();
            Subject getSubject = getByCode(form.getCode());
            if (getSubject != null
                    && !Objects.equals(form.getId(), getSubject.getId())) {
                throw new BizException("科目编码已存在");
            }
            Subject subject = getById(form.getId());
            if (subject == null) {
                throw new BizException("科目不存在");
            }
            subject.setCode(form.getCode());
            Subject parentSubject = getById(subject.getPid());
            if (subject.getPid() > 0 && parentSubject == null) {
                throw new BizException("上级科目不存在");
            }
            //检查科目编码格式
            SubjectCalculateConfigVo parentSubjectCalculateConfigVo = new SubjectCalculateConfigVo();
            if (subject.getPid() > 0) {
                parentSubjectCalculateConfigVo = checkSubjectCode(parentSubject, subject);
            }

            //如果上级科目启用了数量核算
            if (subject.getPid() > 0 && parentSubjectCalculateConfigVo.getEnableNumberCalculateConfig()) {
                if (!form.getSubjectCalculateConfigForm().getEnableNumberCalculateConfig()) {
                    throw new BizException("数量核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentNumberCalculateConfigFlag(true);
                form.getSubjectCalculateConfigForm()
                        .setNumberCalculateConfig(
                                objectConvertor.toNumberCalculateConfig(parentSubjectCalculateConfigVo.getNumberCalculateConfig()));
            }
            //如果科目启用了数量核算
            if (form.getSubjectCalculateConfigForm().getEnableNumberCalculateConfig()
                    && (form.getSubjectCalculateConfigForm().getNumberCalculateConfig() == null
                    || Strings.isBlank(form.getSubjectCalculateConfigForm().getNumberCalculateConfig().getUnitOfMeasurement())
            )) {
                throw new BizException("请设置数量核算单位名称！");
            }

            //如果上级科目启用了辅助核算
            if (subject.getPid() > 0 && parentSubjectCalculateConfigVo.getEnableAssistCalculateConfigs()) {
                if (!form.getSubjectCalculateConfigForm().getEnableAssistCalculateConfigs()) {
                    throw new BizException("辅助核算配置非法");
                }
                if (CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getAssistCalculateConfigs())) {
                    throw new BizException("辅助核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentAssistCalculateConfigsFlag(true);
                //获取上级科目辅助核算配置
                List<SubjectCalculateConfigForm.AssistCalculateConfig> parentAssistCalculateConfigList = objectConvertor
                        .toAssistCalculateConfig(parentSubjectCalculateConfigVo.getAssistCalculateConfigs());
                for (SubjectCalculateConfigForm.AssistCalculateConfig assistCalculateConfig : parentAssistCalculateConfigList) {
                    //获取前端传过来的辅助核算类别id在上级科目所设置的辅助核算是否能找到
                    SubjectCalculateConfigForm.AssistCalculateConfig formAssistCalculateConfig = form.getSubjectCalculateConfigForm()
                            .getAssistCalculateConfigs().stream()
                            .filter(p -> p.getAssistCalculateId().equals(assistCalculateConfig.getAssistCalculateId()))
                            .findFirst().orElseThrow(() -> new BizException("辅助核算配置非法"));
                    //辅助核算类别是否必填，只能通过前端传递不能继承上级科目所设置的辅助核算必填项
                    assistCalculateConfig.setRequiredFlag(formAssistCalculateConfig.getRequiredFlag());
                }
                form.getSubjectCalculateConfigForm().setAssistCalculateConfigs(parentAssistCalculateConfigList);
            }
            //如果启用了辅助核算配置
            Set<Long> assistCalculateCateIds = null;
            if (form.getSubjectCalculateConfigForm().getEnableAssistCalculateConfigs()) {
                if (CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getAssistCalculateConfigs())) {
                    throw new BizException("请设置辅助核算的具体核算项！");
                }
                assistCalculateCateIds = form.getSubjectCalculateConfigForm().getAssistCalculateConfigs().stream()
                        .map(SubjectCalculateConfigForm.AssistCalculateConfig::getAssistCalculateId).collect(Collectors.toSet());
                List<AssistCalculateCate> assistCalculateCates = assistCalculateCateService.list(assistCalculateCateIds);
                if (assistCalculateCateIds.size() != assistCalculateCates.size()) {
                    throw new BizException("辅助核算参数非法");
                }
            }

            //如果上级科目启用了外币核算
            if (subject.getPid() > 0 && parentSubjectCalculateConfigVo.getEnableForeignCurrencyConfig()) {
                if (!form.getSubjectCalculateConfigForm().getEnableForeignCurrencyConfig()) {
                    throw new BizException("外币核算配置非法");
                }
                form.getSubjectCalculateConfigForm().setExtendParentForeignCurrencyConfigFlag(true);
                if (parentSubjectCalculateConfigVo.getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()
                        && !form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()) {
                    throw new BizException("外币核算配置非法");
                }
                Set<Long> parentSubjectCurrencyConfigIds = parentSubjectCalculateConfigVo.getForeignCurrencyConfig().getCurrencyConfigIds();
                form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().setParentSubjectCurrencyConfigIds(parentSubjectCurrencyConfigIds);
                for (Long parentSubjectCurrencyConfigId : parentSubjectCurrencyConfigIds) {
                    form.getSubjectCalculateConfigForm()
                            .getForeignCurrencyConfig().getCurrencyConfigIds().stream().filter(p -> p.equals(parentSubjectCurrencyConfigId))
                            .findFirst().orElseThrow(() -> new BizException("外币核算配置非法"));
                }
            }
            //如果启用了外币核算
            if (form.getSubjectCalculateConfigForm().getEnableForeignCurrencyConfig()) {
                if (form.getSubjectCalculateConfigForm().getForeignCurrencyConfig() == null
                        || CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds())) {
                    throw new BizException("请设置外币核算的具体核算项！");
                }
                List<CurrencyConfig> currencyConfigs = currencyConfigService.list(form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds());
                if (currencyConfigs.size() != form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getCurrencyConfigIds().size()) {
                    throw new BizException("外币核算参数非法");
                }
                if (currencyConfigs.size() == 1 && currencyConfigs.get(0).getBaseCurrencyFlag()) {
                    throw new BizException("不涉及外币，无需设置外币核算");
                }
            } else if (!form.getSubjectCalculateConfigForm().getEnableAssistCalculateConfigs()
                    && form.getSubjectCalculateConfigForm().getForeignCurrencyConfig().getEndOfYearCurrencyRevaluationFlag()) {
                throw new BizException("只要启用了外币核算才能启用期末调汇");
            }

            MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
            wrapper.update(UpdateMemberId, tokenService.getThreadLocalUserId())
                    .update(UpdateTime, DateUtil.getSystemTime())
                    .whereBuilder()
                    .andEq(Id, form.getId())
                    .andEq(TenantId, tokenService.getThreadLocalTenantId());
            //科目没有被使用才能修改辅助核算配置
            if (subject.getUseCount() == 0) {
                wrapper.update(CalculateConfig, jsonMapper.writeValueAsString(form.getSubjectCalculateConfigForm()));
                wrapper.and().andEq(UseCount, 0);
            }
            // 如果是非系统科目
            if (!Objects.equals(subject.getSubjectType(), SubjectTypeEnum.SYSTEM.getCode())) {
                wrapper.update(Name, form.getName())
                        .update(setCode(form.getCode()))
                        .update(setMnemonicCode(form.getMnemonicCode()))
                        .update(setBalanceDirection(form.getBalanceDirection()));
            }
            Optional<SubjectCalculateConfig> subjectCalculateConfig = Optional.ofNullable(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfig.class));
            //获取数据库已保存的外币辅助核算id列表
            Set<Long> oldAssistCalculateCateIds = subjectCalculateConfig.filter(p -> p.getAssistCalculateConfigs() != null)
                    .map(config ->
                            config.getAssistCalculateConfigs().stream()
                                    .map(SubjectCalculateConfig.AssistCalculateConfig::getAssistCalculateId)
                                    .collect(Collectors.toSet())
                    ).orElse(new HashSet<>());

            Set<Long> oldCurrencyConfigIds = subjectCalculateConfig
                    .filter(p -> p.getForeignCurrencyConfig() != null)
                    .map(SubjectCalculateConfig::getForeignCurrencyConfig)
                    .map(SubjectCalculateConfig.ForeignCurrencyConfig::getCurrencyConfigIds).orElse(new HashSet<>());

            Set<Long> finalAssistCalculateCateIds = assistCalculateCateIds;
            transactionTemplate.execute((transactionStatus) -> {
                if (mapper.updateField(wrapper) == 0) {
                    throw new BizException("修改失败");
                }

                if (!CollectionUtils.isEmpty(oldAssistCalculateCateIds)) {
                    //扣减辅助核算类别引用计数
                    assistCalculateCateService.deductUseCount(oldAssistCalculateCateIds);
                }
                if (!CollectionUtils.isEmpty(finalAssistCalculateCateIds)) {
                    //增加辅助核算类别引用计数
                    assistCalculateCateService.addUseCount(finalAssistCalculateCateIds);
                }

                //扣减旧币别的引用计数
                if (!CollectionUtils.isEmpty(oldCurrencyConfigIds)) {
                    currencyConfigService.decrUseCount(form.getSubjectCalculateConfigForm()
                            .getForeignCurrencyConfig().getCurrencyConfigIds());
                }
                //增加币别使用计数
                if (form.getSubjectCalculateConfigForm() != null &&
                        form.getSubjectCalculateConfigForm().getForeignCurrencyConfig() != null
                        && !CollectionUtils.isEmpty(form.getSubjectCalculateConfigForm()
                        .getForeignCurrencyConfig().getCurrencyConfigIds())) {
                    currencyConfigService.addUseCount(form.getSubjectCalculateConfigForm()
                            .getForeignCurrencyConfig().getCurrencyConfigIds());
                }
                return true;
            });
            return true;
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException("修改科目失败", ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 查询科目列表
     *
     * @return
     */
    @Override
    public List<ListSubjectVo> list(ListSubjectForm form) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        Criteria<Subject> criteria = wrapper.select(Id, Pid, Code, Name, MnemonicCode, BalanceDirection, CalculateConfig,
                SubjectCate, Sort, Disable, Level, SubjectType, NodeDepth,
                UseCount, ParentSubjectDisable)
                .whereBuilder()
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        if (form != null && form.getSubjectCate() != null) {
            criteria.andEq(setSubjectCate(form.getSubjectCate()));
        }
        wrapper.orderByAsc(Code);
        List<Subject> subjects = mapper.list(wrapper);
        if (Objects.isNull(subjects)) {
            return null;
        }
        //辅助核算类别列表
        List<ListCalculateCateVo> listCalculateCateVos = assistCalculateCateService.list();

        //币别列表
        List<ListCurrencyConfigVo> listCurrencyConfigVos = currencyConfigService.list();
        List<ListSubjectVo> result = new ArrayList<>();
        for (Subject subject : subjects) {
            ListSubjectVo listSubjectVo = objectConvertor.toListSubjectVo(subject);
            //余额方向
            listSubjectVo.setBalanceDirectionText(BalanceDirectionEnum.getMessage(subject.getBalanceDirection()));
            listSubjectVo.setSubjectCateText(SubjectCateEnum.getMessage(subject.getSubjectCate()));
            try {
                if (Objects.nonNull(subject.getCalculateConfig())) {
                    Optional<SubjectCalculateConfig> subjectCalculateConfig = Optional.ofNullable(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfig.class));
                    //设置计量单位
                    String unitOfMeasurement = subjectCalculateConfig.flatMap(config ->
                            Optional.ofNullable(config.getNumberCalculateConfig())
                                    .map(SubjectCalculateConfig.NumberCalculateConfig::getUnitOfMeasurement))
                            .orElse(null);
                    listSubjectVo.setUnitOfMeasurement(unitOfMeasurement);
                    if (!CollectionUtils.isEmpty(listCalculateCateVos)) {
                        List<Long> assistCalculateCateIds = subjectCalculateConfig
                                .map(SubjectCalculateConfig::getAssistCalculateConfigs)
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(SubjectCalculateConfig.AssistCalculateConfig::getAssistCalculateId)
                                .collect(Collectors.toList());
                        //辅助核算
                        listSubjectVo.setAssistCalculateText(
                                listCalculateCateVos.stream().filter(p ->
                                        assistCalculateCateIds.contains(p.getId())).map(ListCalculateCateVo::getName)
                                        .collect(Collectors.joining(","))
                        );
                    }
                    //外币核算
                    listSubjectVo.setEndOfYearCurrencyRevaluationFlag(subjectCalculateConfig
                            .map(SubjectCalculateConfig::getForeignCurrencyConfig)
                            .map(SubjectCalculateConfig.ForeignCurrencyConfig::getEndOfYearCurrencyRevaluationFlag)
                            .orElse(null));
                    if (!CollectionUtils.isEmpty(listCurrencyConfigVos)) {
                        listSubjectVo.setCurrencyConfigText(
                                listCurrencyConfigVos.stream().filter(p ->
                                        subjectCalculateConfig.map(SubjectCalculateConfig::getForeignCurrencyConfig)
                                                .map(SubjectCalculateConfig.ForeignCurrencyConfig::getCurrencyConfigIds)
                                                .orElse(new HashSet<>())
                                                .contains(p.getId())).map(ListCurrencyConfigVo::getName)
                                        .collect(Collectors.joining("/")));
                    }
                }
            } catch (Exception ex) {
                log.error("辅助核算转换异常", ex);
            }
            result.add(listSubjectVo);
        }
        return result;
    }

    /**
     * 查询科目列表
     *
     * @param form
     * @return
     */
    @Override
    public List<ListSubjectByCateAndCodeAndNameVo> list(ListSubjectByCateAndCodeAndNameForm form) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Code, Name, MnemonicCode, BalanceDirection, CalculateConfig,
                SubjectCate, Sort, Disable, Level, SubjectType, NodeDepth,
                UseCount, ParentSubjectDisable, PidPath)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(Disable, false);
        if (form != null && form.getSubjectCate() != null) {
            wrapper.and().andEq(setSubjectCate(form.getSubjectCate()));
        }
        if (form != null && Strings.isNotBlank(form.getCodeAndName())) {
            wrapper.and().andLike(setCode("%" + form.getCodeAndName() + "%"))
                    .orLike(setName("%" + form.getCodeAndName() + "%"));
        }
        wrapper.orderByAsc(Code);
        List<Subject> subjects = mapper.list(wrapper);
        Map<Long, String> fullNames = getSubjectFullName(subjects);
        List<ListSubjectByCateAndCodeAndNameVo> result = objectConvertor.toListSubjectByCateAndCodeAndNameVo(subjects);
        for (ListSubjectByCateAndCodeAndNameVo item : result) {
            if (!CollectionUtils.isEmpty(fullNames) && fullNames.containsKey(item.getId())) {
                item.setFullName(fullNames.get(item.getId()));
            }
        }
        return result;
    }

    /**
     * 查询科目列表
     *
     * @return
     */
    @Override
    public GetSubjectVo get(long id) throws JsonProcessingException {
        Subject subject = getById(id);
        if (Objects.isNull(subject)) {
            return null;
        }
        GetSubjectVo result = objectConvertor.toGetSubjectVo(subject);
        result.setSubjectCateName(SubjectCateEnum.getMessage(result.getSubjectCate()));
        result.setSubjectCalculateConfigVo(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfigVo.class));
        if (subject.getPid() <= 0) {
            return result;
        }
        Subject parentSubject = getById(subject.getPid());
        if (parentSubject == null) {
            throw new BizException("父级科目不存在");
        }
        result.setParentName(parentSubject.getName());
        return result;
    }

    /**
     * 查询科目列表
     *
     * @return
     */
    @Override
    public GetSubjectDetailVo getDetail(long id) throws JsonProcessingException {
        Subject subject = getById(id);
        if (Objects.isNull(subject)) {
            return null;
        }
        GetSubjectDetailVo result = objectConvertor.toGetSubjectDetailVo(subject);
        result.setSubjectCateName(SubjectCateEnum.getMessage(result.getSubjectCate()));
        SubjectCalculateConfigVo subjectCalculateConfigVo = jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfigVo.class);

        result.setSubjectCalculateConfigVo(new GetSubjectDetailVo.SubjectCalculateDetailConfigVo());
        result.getSubjectCalculateConfigVo().setEnableAssistCalculateConfigs(subjectCalculateConfigVo.getEnableAssistCalculateConfigs());
        result.getSubjectCalculateConfigVo().setEnableForeignCurrencyConfig(subjectCalculateConfigVo.getEnableForeignCurrencyConfig());
        result.getSubjectCalculateConfigVo().setEnableNumberCalculateConfig(subjectCalculateConfigVo.getEnableNumberCalculateConfig());
        if (subjectCalculateConfigVo.getEnableForeignCurrencyConfig()) {
            List<CurrencyConfig> currencyConfigs = currencyConfigService.list(subjectCalculateConfigVo.getForeignCurrencyConfig().getCurrencyConfigIds());
            result.getSubjectCalculateConfigVo().setForeignCurrencyConfig(objectConvertor.toForeignCurrencyConfigVo(currencyConfigs));
        }
        if (subjectCalculateConfigVo.getEnableAssistCalculateConfigs()) {
            Set<Long> assistCalculateIds = subjectCalculateConfigVo.getAssistCalculateConfigs().stream()
                    .map(SubjectCalculateConfigVo.AssistCalculateConfig::getAssistCalculateId).collect(Collectors.toSet());
            List<AssistCalculateCate> assistCalculateCates = assistCalculateCateService.list(assistCalculateIds);
            result.getSubjectCalculateConfigVo().setAssistCalculateConfigs(objectConvertor.toAssistCalculateConfigVo(assistCalculateCates));

            //找到必填的辅助核算类别id
            List<Long> requiredAssistCalculateIds = subjectCalculateConfigVo.getAssistCalculateConfigs().stream()
                    .filter(SubjectCalculateConfigVo.AssistCalculateConfig::getRequiredFlag)
                    .map(SubjectCalculateConfigVo.AssistCalculateConfig::getAssistCalculateId).collect(Collectors.toList());
            result.getSubjectCalculateConfigVo().getAssistCalculateConfigs()
                    .stream().filter(p -> requiredAssistCalculateIds.contains(p.getId())).forEach(p -> p.setRequiredFlag(true));
        }
        return result;
    }

    /**
     * 查询科目列表
     *
     * @return
     */
    @Override
    public List<GetSubjectVo> list(Set<Long> ids, long tenantId) {
        List<Subject> subjects = listByIds(ids, tenantId);
        if (CollectionUtils.isEmpty(subjects)) {
            return null;
        }
        List<GetSubjectVo> results = new ArrayList<>();
        try {
            for (Subject subject : subjects) {
                GetSubjectVo result = objectConvertor.toGetSubjectVo(subject);
                result.setSubjectCateName(SubjectCateEnum.getMessage(result.getSubjectCate()));
                result.setSubjectCalculateConfigVo(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfigVo.class));
                results.add(result);
            }
        } catch (Exception ex) {
            throw new BizException("查询科目列表详情失败");
        }
        return results;
    }

    /**
     * 查询id，编码，名称
     *
     * @param ids
     * @return
     */
    @Override
    public List<Subject> listIdAndCodeAndName(Set<Long> ids) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Code, Name, PidPath, NodeDepth, CalculateConfig)
                .whereBuilder().andIn(Id, ids)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 查询父
     *
     * @return
     */
    @Override
    public GetSubjectVo getByPid(long pid) throws JsonProcessingException {
        Subject subject = getById(pid);
        if (Objects.isNull(subject)) {
            return null;
        }
        String maxCode = getMaxCodeByPid(subject);
        GetSubjectVo result = new GetSubjectVo();
        result.setCode(maxCode);
        result.setParentName(subject.getName());
        result.setSubjectCateName(SubjectCateEnum.getMessage(subject.getSubjectCate()));
        result.setSubjectCalculateConfigVo(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfigVo.class));
//        result.setParentSubjectCalculateConfigVo(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfigVo.class));
        SubjectCalculateConfigVo subjectCalculateConfigVo = result.getSubjectCalculateConfigVo();
        //如果启用了数量核算
        if (subjectCalculateConfigVo.getEnableNumberCalculateConfig()) {
            subjectCalculateConfigVo.setExtendParentNumberCalculateConfigFlag(true);
        } else {
            subjectCalculateConfigVo.setNumberCalculateConfig(new SubjectCalculateConfigVo.NumberCalculateConfig());
        }
        //如果启用了辅助核算
        if (subjectCalculateConfigVo.getEnableAssistCalculateConfigs()) {
            subjectCalculateConfigVo.setExtendParentAssistCalculateConfigsFlag(true);
            for (SubjectCalculateConfigVo.AssistCalculateConfig assistCalculateConfig :
                    subjectCalculateConfigVo.getAssistCalculateConfigs()) {
                assistCalculateConfig.setRequiredFlag(false);
            }
        } else {
            subjectCalculateConfigVo.setAssistCalculateConfigs(new ArrayList<>());
        }

        //如果启用了外币核算
        if (subjectCalculateConfigVo.getEnableForeignCurrencyConfig()) {
            subjectCalculateConfigVo.setExtendParentForeignCurrencyConfigFlag(true);
            Set<Long> parentSubjectCurrencyConfigIds = subjectCalculateConfigVo.getForeignCurrencyConfig().getCurrencyConfigIds();
            subjectCalculateConfigVo.getForeignCurrencyConfig().setParentSubjectCurrencyConfigIds(parentSubjectCurrencyConfigIds);
//            subjectCalculateConfigVo.getForeignCurrencyConfig().setCurrencyConfigIds(parentSubjectCurrencyConfigIds);
        } else {
            subjectCalculateConfigVo.setForeignCurrencyConfig(new SubjectCalculateConfigVo.ForeignCurrencyConfig());
        }
        return result;
    }

    /**
     * 增加科目使用计数
     *
     * @param id
     * @return
     */
    @Override
    public boolean addUseCount(long id) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(setUseCount(1))
                .whereBuilder().andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.updateField(wrapper) > 0;
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
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(setUseCount(count))
                .whereBuilder().andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("增加科目使用计数失败");
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
    public boolean addUseCount(Set<Long> ids) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(setUseCount(1))
                .whereBuilder().andIn(Id, ids)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.updateField(wrapper) > 0;
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
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(setUseCount(count));
        wrapper.whereBuilder().andEq(setId(id))
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andGTE(setUseCount(count));
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 导出科目
     *
     * @param response
     */
    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<DownloadSubjectVo> data = download();
        MyUtil.getDownloadExcelResponse(response, "科目");
        EasyExcel.write(response.getOutputStream(), DownloadSubjectVo.class)
                .sheet("科目")
                .doWrite(data);
    }

    /**
     * 获取科目全名称（含父级名称）
     *
     * @param subjects
     * @return
     */
    @Override
    public Map<Long, String> getSubjectFullName(List<Subject> subjects) {
        if (CollectionUtils.isEmpty(subjects)) {
            return null;
        }
        Map<Long, String> result = new HashMap<>();
        Map<Long, List<Subject>> parentSubjectList = getParentSubjectList(subjects);
        for (Map.Entry<Long, List<Subject>> entry : parentSubjectList.entrySet()) {
            Long key = entry.getKey();
            List<Subject> subjectList = entry.getValue();
            StringBuilder fullName = new StringBuilder();
            // 如果需要，你可以进一步遍历List<Subject>
            for (Subject subject : subjectList) {
                if (fullName.length() == 0) {
                    fullName.append(subject.getName());
                } else {
                    fullName.append("-").append(subject.getName());
                }
            }
            result.put(key, fullName.toString());
        }
        return result;
    }

    /**
     * 获取科目全名称（含父级名称）
     *
     * @param subjects
     * @return
     */
    @Override
    public Map<Long, List<Subject>> getParentSubjectList(List<Subject> subjects) {
        if (CollectionUtils.isEmpty(subjects)) {
            return null;
        }
        Map<Long, List<Subject>> result = new HashMap<>();
        subjects.forEach(item -> {
            List<Subject> subjectList = new ArrayList<>();
            subjectList.add(item);
            result.put(item.getId(), subjectList);
        });
        Set<Long> pids = subjects.stream()
                .filter(p -> p.getPid() > 0)
                .map(p -> {
                    String pidPathWithoutFirstSlash = p.getPidPath().replaceFirst("/", "");
                    String[] pidPathArray = pidPathWithoutFirstSlash.split("/");
                    return Arrays.stream(pidPathArray)
                            .mapToLong(Long::parseLong)
                            .boxed()
                            .collect(Collectors.toSet());
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(pids)) {
            return result;
        }
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, NodeDepth)
                .whereBuilder()
                .andIn(Id, pids)
                .andEq(DelFlag, false);
        wrapper.orderByAsc(NodeDepth);
        List<Subject> allParentSubjectList = mapper.list(wrapper);
        if (CollectionUtils.isEmpty(allParentSubjectList)) {
            return result;
        }
        subjects.forEach(subject -> {
            if (subject.getPid() > 0) {
                String pidPath = subject.getPidPath().replaceFirst("/", "");
                String[] pidPathArray = pidPath.split("/");
                List<Long> parentIdList = Arrays.stream(pidPathArray)
                        .mapToLong(Long::parseLong)
                        .boxed()
                        .collect(Collectors.toList());
                List<Subject> parentSubjectList = new ArrayList<>();
                parentIdList.forEach(pid -> {
                    allParentSubjectList.stream().filter(p -> p.getId().equals(pid))
                            .findFirst().ifPresent(parentSubjectList::add);
                });
                parentSubjectList.add(subject);
                result.put(subject.getId(), parentSubjectList);
            }
        });
        return result;
    }

    private List<DownloadSubjectVo> download() {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, Code, Name, MnemonicCode, BalanceDirection, CalculateConfig,
                SubjectCate, Sort, Disable, Level, SubjectType, NodeDepth,
                UseCount, ParentSubjectDisable)
                .whereBuilder()
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        wrapper.orderByAsc(Code);
        List<Subject> subjects = mapper.list(wrapper);
        if (Objects.isNull(subjects)) {
            return null;
        }
        //辅助核算类别列表
        List<ListCalculateCateVo> listCalculateCateVos = assistCalculateCateService.list();

        //币别列表
        List<ListCurrencyConfigVo> listCurrencyConfigVos = currencyConfigService.list();
        List<DownloadSubjectVo> result = new ArrayList<>();
        for (Subject subject : subjects) {
            String parentSubjectName = subjects.stream().filter(p -> p.getId().equals(subject.getPid()))
                    .map(Subject::getName)
                    .findFirst().orElse(null);
            DownloadSubjectVo listSubjectVo = objectConvertor.toDownloadSubjectVo(subject);
            listSubjectVo.setParentSubjectName(parentSubjectName);
            //余额方向
            listSubjectVo.setBalanceDirectionText(BalanceDirectionEnum.getMessage(subject.getBalanceDirection()));
            listSubjectVo.setSubjectCateText(SubjectCateEnum.getMessage(subject.getSubjectCate()));
            try {
                if (Objects.nonNull(subject.getCalculateConfig())) {
                    Optional<SubjectCalculateConfig> subjectCalculateConfig = Optional.ofNullable(jsonMapper.readValue(subject.getCalculateConfig(), SubjectCalculateConfig.class));
                    //设置计量单位
                    String unitOfMeasurement = subjectCalculateConfig.flatMap(config ->
                            Optional.ofNullable(config.getNumberCalculateConfig())
                                    .map(SubjectCalculateConfig.NumberCalculateConfig::getUnitOfMeasurement))
                            .orElse(null);
                    listSubjectVo.setUnitOfMeasurement(unitOfMeasurement);
                    if (!CollectionUtils.isEmpty(listCalculateCateVos)) {
                        List<Long> assistCalculateCateIds = subjectCalculateConfig
                                .map(SubjectCalculateConfig::getAssistCalculateConfigs)
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(SubjectCalculateConfig.AssistCalculateConfig::getAssistCalculateId)
                                .collect(Collectors.toList());
                        //辅助核算
                        listSubjectVo.setAssistCalculateText(
                                listCalculateCateVos.stream().filter(p ->
                                        assistCalculateCateIds.contains(p.getId())).map(ListCalculateCateVo::getName)
                                        .collect(Collectors.joining(","))
                        );
                    }
                    //外币核算
                    boolean endOfYearCurrencyRevaluationFlag = subjectCalculateConfig
                            .map(SubjectCalculateConfig::getForeignCurrencyConfig)
                            .map(SubjectCalculateConfig.ForeignCurrencyConfig::getEndOfYearCurrencyRevaluationFlag)
                            .orElse(false);
                    listSubjectVo.setEndOfYearCurrencyRevaluationFlagText(
                            endOfYearCurrencyRevaluationFlag ? "是" : "否");
                    if (Objects.nonNull(listCurrencyConfigVos)) {
                        listSubjectVo.setCurrencyConfigText(
                                listCurrencyConfigVos.stream().filter(p ->
                                        subjectCalculateConfig.map(SubjectCalculateConfig::getForeignCurrencyConfig)
                                                .map(SubjectCalculateConfig.ForeignCurrencyConfig::getCurrencyConfigIds)
                                                .orElse(new HashSet<>())
                                                .contains(p.getId())).map(ListCurrencyConfigVo::getName)
                                        .collect(Collectors.joining("/")));
                    }
                }
            } catch (Exception ex) {
                log.error("辅助核算转换异常", ex);
            }
            result.add(listSubjectVo);
        }
        return result;
    }

    /**
     * 查询科目信息
     *
     * @param code
     * @return
     */
    private Subject getByCode(String code) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, Code, Name)
                .whereBuilder()
                .andEq(setCode(code))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        return mapper.get(wrapper);
    }

    /**
     * 查询科目信息
     *
     * @param id
     * @return
     */
    private Subject getById(long id) {
        if (id <= 0) {
            return null;
        }
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, Code, Name, Pid, MnemonicCode, SubjectCate,
                        BalanceDirection, Disable, CalculateConfig, NodeDepth, Sort,
                        SubjectType, ParentSubjectDisable, PidPath, UseCount)
                .whereBuilder()
                .andEq(setId(id))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        return mapper.get(wrapper);
    }

    /**
     * 检查科目编码
     *
     * @param subject
     * @return 返回上级科目
     */
    private SubjectCalculateConfigVo checkSubjectCode(Subject parentSubject, Subject subject) throws JsonProcessingException {
        if (parentSubject == null) {
            throw new BizException("上级科目不存在");
        }
        if (Strings.isBlank(parentSubject.getCode())) {
            throw new BizException("上级科目编码异常");
        }
        subject.setNodeDepth(parentSubject.getNodeDepth() + 1);

        subject.setSubjectCate(parentSubject.getSubjectCate());

        SubjectCodeLengthConfigVo subjectCodeLengthConfig = tenantSysConfigService.getSubjectCodeLengthConfig();
        if (subject.getNodeDepth() == 0) {
            if (subject.getCode().length() != subjectCodeLengthConfig.getDepthCodeLengthOne()) {
                throw new BizException("科目编码格式不正确");
            }
            subject.setDepthCodeLengthOne(subjectCodeLengthConfig.getDepthCodeLengthOne());
        }
        int totalLength = subjectCodeLengthConfig.getDepthCodeLengthOne() + subjectCodeLengthConfig.getDepthCodeLengthTwo();
        if (subject.getNodeDepth() == 1) {
            if (subject.getCode().length() != totalLength || subject.getCode().indexOf(parentSubject.getCode()) != 0) {
                throw new BizException("科目编码格式不正确");
            }
            subject.setDepthCodeLengthOne(subjectCodeLengthConfig.getDepthCodeLengthOne());
            subject.setDepthCodeLengthTwo(subjectCodeLengthConfig.getDepthCodeLengthTwo());
        }
        totalLength = subjectCodeLengthConfig.getDepthCodeLengthOne() + subjectCodeLengthConfig.getDepthCodeLengthTwo()
                + subjectCodeLengthConfig.getDepthCodeLengthThree();
        if (subject.getNodeDepth() == 2) {
            if (subject.getCode().length() != totalLength || subject.getCode().indexOf(parentSubject.getCode()) != 0) {
                throw new BizException("科目编码格式不正确");
            }
            subject.setDepthCodeLengthOne(subjectCodeLengthConfig.getDepthCodeLengthOne());
            subject.setDepthCodeLengthTwo(subjectCodeLengthConfig.getDepthCodeLengthTwo());
            subject.setDepthCodeLengthThree(subjectCodeLengthConfig.getDepthCodeLengthThree());
        }
        totalLength = subjectCodeLengthConfig.getDepthCodeLengthOne() + subjectCodeLengthConfig.getDepthCodeLengthTwo()
                + subjectCodeLengthConfig.getDepthCodeLengthThree() + subjectCodeLengthConfig.getDepthCodeLengthFour();
        if (subject.getNodeDepth() == 3) {
            if (subject.getCode().length() != totalLength || subject.getCode().indexOf(parentSubject.getCode()) != 0) {
                throw new BizException("科目编码格式不正确");
            }
            subject.setDepthCodeLengthOne(subjectCodeLengthConfig.getDepthCodeLengthOne());
            subject.setDepthCodeLengthTwo(subjectCodeLengthConfig.getDepthCodeLengthTwo());
            subject.setDepthCodeLengthThree(subjectCodeLengthConfig.getDepthCodeLengthThree());
            subject.setDepthCodeLengthFour(subjectCodeLengthConfig.getDepthCodeLengthFour());
        }

        SubjectCalculateConfigVo parentSubjectCalculateConfigVo = jsonMapper.readValue(parentSubject.getCalculateConfig(),
                SubjectCalculateConfigVo.class);
        return parentSubjectCalculateConfigVo;
    }

    /**
     * @param ids
     * @return
     */
    private List<Subject> listByIds(String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, CalculateConfig)
                .whereBuilder()
                .andIn(new FieldResult(Id, idList));
        wrapper.orderByDesc(NodeDepth);
        return mapper.list(wrapper);
    }

    /**
     * @param ids
     * @return
     */
    private List<Subject> listByIds(Set<Long> ids, long tenantId) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Pid, CalculateConfig, SubjectCate)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false)
                .andEq(TenantId, tenantId);
        wrapper.orderByDesc(NodeDepth);
        return mapper.list(wrapper);
    }

    /**
     * 获取当前科目下一级子科目的最大code
     *
     * @param parentSubject
     * @return
     */
    private String getMaxCodeByPid(Subject parentSubject) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.max(Code)
                .whereBuilder()
                .andEq(Pid, parentSubject.getId())
                .andEq(DelFlag, false);
        Subject subject = mapper.get(wrapper);
        int nodeDepth = parentSubject.getNodeDepth() + 1;
        if (subject == null) {
            return parentSubject.getCode() + generateCode(nodeDepth, 1);
        } else {
            String code = subject.getCode().replaceFirst(parentSubject.getCode(), "");
            return parentSubject.getCode() + generateCode(nodeDepth, Long.parseLong(code) + 1);
        }
    }

    /**
     * 生成code
     *
     * @param nodePath
     * @param code
     * @return
     */
    private String generateCode(int nodePath, long code) {
        SubjectCodeLengthConfigVo subjectCodeLengthConfig = tenantSysConfigService.getSubjectCodeLengthConfig();
        switch (nodePath) {
            case 0:
                return String.format("%0" + subjectCodeLengthConfig.getDepthCodeLengthOne() + "d", code);
            case 1:
                return String.format("%0" + subjectCodeLengthConfig.getDepthCodeLengthTwo() + "d", code);
            case 2:
                return String.format("%0" + subjectCodeLengthConfig.getDepthCodeLengthThree() + "d", code);
            case 3:
                return String.format("%0" + subjectCodeLengthConfig.getDepthCodeLengthFour() + "d", code);
            default:
                throw new BizException("生成code异常");
        }
    }

    /**
     * 获取禁用或启用的子节点个数
     *
     * @param pidPath
     * @return
     */
    private int getChildCount(long pid, String pidPath) {
        if (Strings.isBlank(pidPath)) {
            pidPath = "/" + pid + "/";
        } else {
            pidPath += pid + "/";
        }
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andLike(PidPath, pidPath + "%")
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.count(wrapper);
    }

    /**
     * 更新父节点下面所有子节点为禁用状态
     *
     * @param pidPath
     * @return
     */
    private int updateChildDisable(long pid, String pidPath) {
        if (Strings.isBlank(pidPath)) {
            pidPath = "/" + pid + "/";
        } else {
            pidPath += pid + "/";
        }
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.update(Disable, true)
                .update(ParentSubjectDisable, true)
                .whereBuilder()
                .andLike(PidPath, pidPath + "%")
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.updateField(wrapper);
    }

    /**
     * 获取一级科目下所有子节点数量
     *
     * @param pid
     * @return
     */
    private int getChildOneCount(long pid) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(Pid, pid)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.count(wrapper);
    }

    /**
     * 更新子科目的父模块禁用启用状态字段为启用状态
     *
     * @param pid
     * @return
     */
    private int updateParentSubjectDisable(long pid) {
        MyBatisWrapper<Subject> wrapper = new MyBatisWrapper<>();
        wrapper.update(ParentSubjectDisable, false)
                .whereBuilder()
                .andEq(Pid, pid)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.updateField(wrapper);
    }
}
