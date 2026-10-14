package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.VoucherWordConfig;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateVoucherWordConfigForm;
import com.bage.finance.biz.dto.form.DelVoucherWordConfigForm;
import com.bage.finance.biz.dto.form.UpdateVoucherWordConfigDefaultFlagForm;
import com.bage.finance.biz.dto.form.UpdateVoucherWordConfigForm;
import com.bage.finance.biz.dto.vo.GetVoucherWordConfigVo;
import com.bage.finance.biz.dto.vo.ListVoucherWordConfigVo;
import com.bage.finance.biz.mapper.VoucherWordConfigMapper;
import com.bage.finance.biz.service.VoucherWordConfigService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.bage.finance.biz.domain.VoucherWordConfigField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherWordConfigServiceImpl implements VoucherWordConfigService {
    final VoucherWordConfigMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final TransactionTemplate transactionTemplate;

    /**
     * 添加凭证字
     *
     * @param form
     * @return
     */
    @Override
    public boolean create(CreateVoucherWordConfigForm form) {
        VoucherWordConfig voucherWordConfig = getByVoucherWord(form.getVoucherWord());
        if (voucherWordConfig != null) {
            throw new BizException("凭证字已存在");
        }
        VoucherWordConfig insertVoucherWordConfig = objectConvertor.toVoucherWordConfig(form);
        insertVoucherWordConfig.initDefault();
        insertVoucherWordConfig.setMemberId(tokenService.getThreadLocalUserId());
        insertVoucherWordConfig.setTenantId(tokenService.getThreadLocalTenantId());
        insertVoucherWordConfig.setDefaultFlag(false);
        Boolean result = transactionTemplate.execute(transactionStatus -> {
            return mapper.insert(insertVoucherWordConfig) > 0;
        });
        return result == null ? false : result;
    }

    /**
     * 删除币别
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelVoucherWordConfigForm form) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateConcat(setVoucherWord("_del_" + form.getId()))
                .update(DelFlag, true)
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, new Date())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(DelFlag, false)
                .andEq(DefaultFlag, false)
                //未使用的才能删除
                .andEq(UseCount, 0);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("删除失败");
        }
        return true;
    }

    /**
     * 修改凭证字
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateVoucherWordConfigForm form) {
        VoucherWordConfig voucherWordConfig = getByVoucherWord(form.getVoucherWord());
        if (voucherWordConfig != null
                && !Objects.equals(form.getId(), voucherWordConfig.getId())) {
            throw new BizException("凭证字已存在");
        }

        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.update(VoucherWord, form.getVoucherWord())
                .update(PrintTitle, form.getPrintTitle())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, new Date())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("修改失败");
        }
        return true;
    }

    /**
     * 修改凭证字默认值(改为默认值)
     *
     * @param form
     * @return
     */
    @Override
    public boolean updateDefaultFlag(UpdateVoucherWordConfigDefaultFlagForm form) {
        transactionTemplate.execute(transactionStatus -> {
            //清空默认凭证，系统一定要保留一个默认凭证，这样会让开发处理简单很多，也符合需求
            if (!clearDefault(form.getId())) {
                throw new BizException("修改默认凭证失败");
            }
            MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
            wrapper
                    .update(DefaultFlag, true)
                    .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                    .update(UpdateTime, new Date())
                    .whereBuilder()
                    .andEq(Id, form.getId())
                    .andEq(TenantId, tokenService.getThreadLocalTenantId())
                    .andEq(DefaultFlag, false)
                    .andEq(DelFlag, false);
            if (mapper.updateField(wrapper) == 0) {
                throw new BizException("修改失败");
            }
            return true;
        });
        return true;
    }

    /**
     * 查询凭证字列表
     *
     * @return
     */
    @Override
    public List<ListVoucherWordConfigVo> list() {
        MyBatisWrapper<VoucherWordConfig> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(Id, VoucherWord, PrintTitle, DefaultFlag, UseCount)
                .whereBuilder().andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        List<VoucherWordConfig> voucherWordConfigs = mapper.list(myBatisWrapper);
        return objectConvertor.toListVoucherWordConfigVo(voucherWordConfigs);
    }

    /**
     * 根据id查询凭证字列表
     *
     * @param ids
     * @return
     */
    @Override
    public List<VoucherWordConfig> listByIds(Set<Long> ids) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherWord)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.list(wrapper);
    }

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    @Override
    public GetVoucherWordConfigVo getById(long id) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, VoucherWord, PrintTitle, DefaultFlag)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        VoucherWordConfig voucherWordConfig = mapper.get(wrapper);
        if (voucherWordConfig == null) {
            throw new BizException("凭证字不存在");
        }
        return objectConvertor.toGetVoucherWordConfigVo(voucherWordConfig);
    }

    /**
     * 增加凭证字使用计数
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public boolean addUseCount(long id, int count) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(setUseCount(count))
                .whereBuilder()
                .andEq(Id, id);
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 减少凭证字使用计数
     *
     * @param id
     * @return
     */
    @Override
    public boolean deductUseCount(long id, int count) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(setUseCount(count));
        wrapper.whereBuilder().andEq(setId(id))
                .andGT(setUseCount(count));
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    @Override
    public VoucherWordConfig getVoucherWordConfig(long id) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, VoucherWord, PrintTitle, DefaultFlag)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false);
        VoucherWordConfig voucherWordConfig = mapper.get(wrapper);
        if (voucherWordConfig == null) {
            throw new BizException("凭证字不存在");
        }
        return voucherWordConfig;
    }

    /**
     * 查询凭证字信息
     *
     * @param voucherWord
     * @return
     */
    private VoucherWordConfig getByVoucherWord(String voucherWord) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, VoucherWord, PrintTitle)
                .whereBuilder()
                .andEq(setVoucherWord(voucherWord))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        return mapper.get(wrapper);
    }

    /**
     * 清空默认凭证字
     *
     * @return
     */
    private boolean clearDefault(long id) {
        MyBatisWrapper<VoucherWordConfig> wrapper = new MyBatisWrapper<>();
        wrapper.update(DefaultFlag, false)
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, new Date())
                .whereBuilder()
                .andEq(DefaultFlag, true)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andNE(Id, id);
        return mapper.updateField(wrapper) > 0;
    }
}
