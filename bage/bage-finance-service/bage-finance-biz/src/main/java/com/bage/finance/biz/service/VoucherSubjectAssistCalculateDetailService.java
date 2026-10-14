package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.VoucherSubjectAssistCalculateDetail;

import java.util.List;
import java.util.Set;

public interface VoucherSubjectAssistCalculateDetailService {
    /**
     * 根据凭证id列表查询凭证科目辅助核算明细列表
     *
     * @param voucherIds
     * @return
     */
    List<VoucherSubjectAssistCalculateDetail> listByVoucherIds(Set<Long> voucherIds);

    /**
     * 根据凭证id查询凭证科目辅助核算明细列表
     *
     * @param voucherId
     * @return
     */
    List<VoucherSubjectAssistCalculateDetail> listByVoucherId(Long voucherId);

    /**
     * 删除凭证科目辅助核算明细
     *
     * @param voucherId
     * @return
     */
    boolean del(long voucherId);
}