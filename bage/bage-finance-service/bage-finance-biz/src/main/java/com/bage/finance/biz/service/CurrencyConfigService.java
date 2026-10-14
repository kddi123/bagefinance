package com.bage.finance.biz.service;


import com.bage.finance.biz.domain.CurrencyConfig;
import com.bage.finance.biz.dto.form.CreateCurrencyConfigForm;
import com.bage.finance.biz.dto.form.DelCurrencyConfigForm;
import com.bage.finance.biz.dto.form.UpdateCurrencyConfigForm;
import com.bage.finance.biz.dto.vo.GetCurrencyConfigVo;
import com.bage.finance.biz.dto.vo.ListCurrencyConfigVo;

import java.util.List;
import java.util.Set;

public interface CurrencyConfigService {
    /**
     * 添加币别
     * @param form
     * @return
     */
    boolean create(CreateCurrencyConfigForm form);

    /**
     * 删除币别
     *
     * @param form
     * @return
     */
    boolean del(DelCurrencyConfigForm form);

    /**
     * 修改币别
     *
     * @param form
     * @return
     */
    boolean update(UpdateCurrencyConfigForm form);

    /**
     * 查询币别列表
     * @return
     */
    List<ListCurrencyConfigVo> list(long tenantId);

    /**
     * 查询币别列表
     * @return
     */
    List<ListCurrencyConfigVo> list();

    /**
     * 查询币别列表
     *
     * @return
     */
    List<CurrencyConfig> list(Set<Long> ids);

    /**
     * 查询币别列表(和租户无关)
     *
     * @return
     */
    List<CurrencyConfig> listByIds(Set<Long> ids);

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    GetCurrencyConfigVo getById(Long id);

    /**
     * 增加币别使用计数
     *
     * @param ids
     * @return
     */
    boolean addUseCount(Set<Long> ids);

    /**
     * 扣减币别使用计数
     *
     * @param ids
     * @return
     */
    boolean decrUseCount(Set<Long> ids);

    /**
     * 增加币别使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean addUseCount(long id, int count);

    /**
     * 减少币别使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean decrUseCount(long id, int count);
}
