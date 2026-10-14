package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.CurrencyConfig;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateCurrencyConfigForm;
import com.bage.finance.biz.dto.form.DelCurrencyConfigForm;
import com.bage.finance.biz.dto.form.UpdateCurrencyConfigForm;
import com.bage.finance.biz.dto.vo.GetCurrencyConfigVo;
import com.bage.finance.biz.dto.vo.ListCurrencyConfigVo;
import com.bage.finance.biz.mapper.CurrencyConfigMapper;
import com.bage.finance.biz.service.CurrencyConfigService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.bage.finance.biz.domain.CurrencyConfigField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConfigServiceImpl implements CurrencyConfigService {
    final CurrencyConfigMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;

    /**
     * 添加币别
     *
     * @param form
     * @return
     */
    @Override
    public boolean create(CreateCurrencyConfigForm form) {
        GetCurrencyConfigVo getCurrencyConfigVo = getByCode(form.getCode());
        if (getCurrencyConfigVo != null) {
            throw new BizException("币别已存在");
        }
        CurrencyConfig currencyConfig = objectConvertor.toCurrencyConfig(form);
        currencyConfig.initDefault();
        currencyConfig.setMemberId(tokenService.getThreadLocalUserId());
        currencyConfig.setTenantId(tokenService.getThreadLocalTenantId());
        return mapper.insert(currencyConfig) > 0;
    }

    /**
     * 删除币别
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelCurrencyConfigForm form) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateConcat(setCode("_del_" + form.getId()))
                .update(DelFlag, true)
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, new Date())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andEq(BaseCurrencyFlag, false)
                .andEq(UseCount, 0);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("删除失败");
        }
        return true;
    }

    /**
     * 修改币别
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateCurrencyConfigForm form) {
        GetCurrencyConfigVo getCurrencyConfigVo = getByCode(form.getCode());
        if (getCurrencyConfigVo != null
                && !Objects.equals(form.getId(), getCurrencyConfigVo.getId())) {
            throw new BizException("币别已存在");
        }
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.update(Code, form.getCode())
                .update(setName(form.getName()))
                .update(setExchangeRate(form.getExchangeRate()))
                .update(setUpdateMemberId(tokenService.getThreadLocalUserId()))
                .update(setUpdateTime(new Date()))
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()));
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("修改失败");
        }
        return true;
    }

    /**
     * 查询币别列表
     *
     * @return
     */
    @Override
    public List<ListCurrencyConfigVo> list(long tenantId) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Code, Name, ExchangeRate, BaseCurrencyFlag, UseCount)
                .whereBuilder()
                .andEq(setTenantId(tenantId))
                .andEq(setDelFlag(false));
        wrapper.orderByDesc(BaseCurrencyFlag)
                .orderByAsc(Code);
        List<CurrencyConfig> currencyConfigs = mapper.list(wrapper);
        return objectConvertor.toListCurrencyConfigVo(currencyConfigs);
    }

    /**
     * 查询币别列表
     *
     * @return
     */
    @Override
    public List<ListCurrencyConfigVo> list() {
        return list(tokenService.getThreadLocalTenantId());
    }

    /**
     * 查询币别列表
     *
     * @return
     */
    @Override
    public List<CurrencyConfig> list(Set<Long> ids) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Code, Name, ExchangeRate, BaseCurrencyFlag, UseCount)
                .whereBuilder()
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false))
                .andIn(Id, ids);
        return mapper.list(wrapper);
    }

    /**
     * 查询币别列表(和租户无关)
     *
     * @param ids
     * @return
     */
    @Override
    public List<CurrencyConfig> listByIds(Set<Long> ids) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Code, Name, ExchangeRate, BaseCurrencyFlag, UseCount)
                .whereBuilder()
                .andEq(setDelFlag(false))
                .andIn(Id, ids);
        return mapper.list(wrapper);
    }

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    @Override
    public GetCurrencyConfigVo getById(Long id) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, Code, Name, ExchangeRate, BaseCurrencyFlag)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        CurrencyConfig currencyConfig = mapper.get(wrapper);
        if (currencyConfig == null) {
            throw new BizException("币别不存在");
        }
        return objectConvertor.toGetCurrencyConfigVo(currencyConfig);
    }

    /**
     * 增加币别使用计数
     *
     * @param ids
     * @return
     */
    @Override
    public boolean addUseCount(Set<Long> ids) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(UseCount, 1)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        int updateRows = mapper.updateField(wrapper);
        if (ids.size() != updateRows) {
            throw new BizException("增加币别使用计数异常");
        }
        return true;
    }

    /**
     * 扣减币别使用计数
     *
     * @param ids
     * @return
     */
    @Override
    public boolean decrUseCount(Set<Long> ids) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(UseCount, 1)
                .whereBuilder()
                .andIn(Id, ids)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andGT(UseCount, 0);
        int updateRows = mapper.updateField(wrapper);
        if (ids.size() != updateRows) {
            throw new BizException("增加币别使用计数异常");
        }
        return true;
    }

    /**
     * 增加币别使用计数
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public boolean addUseCount(long id, int count) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(UseCount, count)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("增加币别使用计数失败");
        }
        return true;
    }

    /**
     * 减少币别使用计数
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public boolean decrUseCount(long id, int count) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper.updateDecr(UseCount, count)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andGT(UseCount, count);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("减少币别使用计数失败");
        }
        return true;
    }

    /**
     * 查询币别信息
     *
     * @param code
     * @return
     */
    private GetCurrencyConfigVo getByCode(String code) {
        MyBatisWrapper<CurrencyConfig> wrapper = new MyBatisWrapper<>();
        wrapper
                .select(Id, Code, Name, ExchangeRate, BaseCurrencyFlag)
                .whereBuilder()
                .andEq(setCode(code))
                .andEq(setTenantId(tokenService.getThreadLocalTenantId()))
                .andEq(setDelFlag(false));
        CurrencyConfig currencyConfig = mapper.get(wrapper);
        return objectConvertor.toGetCurrencyConfigVo(currencyConfig);
    }
}
