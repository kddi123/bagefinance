package com.bage.finance.biz.service;


import com.bage.finance.biz.domain.VoucherWordConfig;
import com.bage.finance.biz.dto.form.CreateVoucherWordConfigForm;
import com.bage.finance.biz.dto.form.DelVoucherWordConfigForm;
import com.bage.finance.biz.dto.form.UpdateVoucherWordConfigDefaultFlagForm;
import com.bage.finance.biz.dto.form.UpdateVoucherWordConfigForm;
import com.bage.finance.biz.dto.vo.GetVoucherWordConfigVo;
import com.bage.finance.biz.dto.vo.ListVoucherWordConfigVo;

import java.util.List;
import java.util.Set;

public interface VoucherWordConfigService {
    /**
     * 添加凭证字
     *
     * @param form
     * @return
     */
    boolean create(CreateVoucherWordConfigForm form);

    /**
     * 删除币别
     *
     * @param form
     * @return
     */
    boolean del(DelVoucherWordConfigForm form);

    /**
     * 修改币别
     *
     * @param form
     * @return
     */
    boolean update(UpdateVoucherWordConfigForm form);

    /**
     * 修改凭证字默认值
     *
     * @param form
     * @return
     */
    boolean updateDefaultFlag(UpdateVoucherWordConfigDefaultFlagForm form);

    /**
     * 查询凭证字列表
     *
     * @return
     */
    List<ListVoucherWordConfigVo> list();

    /**
     * 根据id查询凭证字列表
     *
     * @param ids
     * @return
     */
    List<VoucherWordConfig> listByIds(Set<Long> ids);

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    GetVoucherWordConfigVo getById(long id);

    /**
     * 增加凭证字使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean addUseCount(long id, int count);

    /**
     * 减少凭证字使用计数
     *
     * @param id
     * @param count
     * @return
     */
    boolean deductUseCount(long id, int count);

    /**
     * 查询币别信息
     *
     * @param id
     * @return
     */
    VoucherWordConfig getVoucherWordConfig(long id);
}
