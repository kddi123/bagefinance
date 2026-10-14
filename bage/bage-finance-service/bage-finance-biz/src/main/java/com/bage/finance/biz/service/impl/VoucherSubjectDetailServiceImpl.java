package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.VoucherSubjectDetail;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.mapper.VoucherSubjectDetailMapper;
import com.bage.finance.biz.service.VoucherSubjectDetailService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.bage.finance.biz.domain.VoucherSubjectDetailField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherSubjectDetailServiceImpl implements VoucherSubjectDetailService {
    final VoucherSubjectDetailMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;

    /**
     * 根据凭证id列表查询凭证科目明细
     *
     * @param voucherIds
     * @return
     */
    @Override
    public List<VoucherSubjectDetail> listByVoucherIds(Set<Long> voucherIds) {
        MyBatisWrapper<VoucherSubjectDetail> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherId, SubjectId, CurrencyConfigId, ExchangeRate, OriginalCurrency, SubjectNum,
                SubjectUnitPrice, Balance, DebitAmount, CreditAmount, Summary, EnableAssistCalculateConfigs, EnableForeignCurrencyConfig,
                EnableNumberCalculateConfig);
        wrapper.whereBuilder().andIn(VoucherId, voucherIds)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 根据凭证id查询凭证科目明细
     *
     * @param voucherId
     * @return
     */
    @Override
    public List<VoucherSubjectDetail> listByVoucherId(Long voucherId) {
        MyBatisWrapper<VoucherSubjectDetail> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherId, SubjectId, CurrencyConfigId, Summary, OriginalCurrency,
                ExchangeRate, OriginalCurrency, SubjectNum, SubjectUnitPrice, Balance,
                DebitAmount, CreditAmount);
        wrapper.whereBuilder()
                .andEq(VoucherId, voucherId)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

    /**
     * 删除凭证科目明细
     *
     * @param voucherId
     * @return
     */
    @Override
    public boolean del(long voucherId) {
        MyBatisWrapper<VoucherSubjectDetail> wrapper = new MyBatisWrapper<>();
        wrapper.update(DelFlag, true)
                .update(MemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder()
                .andEq(VoucherId, voucherId)
                .andEq(DelFlag, false);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("删除凭证科目明细失败");
        }
        return true;
    }
}
