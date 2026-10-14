package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.VoucherSubjectDetail;

import java.util.List;
import java.util.Set;

public interface VoucherSubjectDetailService {
    /**
     * 根据凭证id列表查询凭证科目明细
     *
     * @param voucherIds
     * @return
     */
    List<VoucherSubjectDetail> listByVoucherIds(Set<Long> voucherIds);

    /**
     * 根据凭证id查询凭证科目明细
     *
     * @param voucherId
     * @return
     */
    List<VoucherSubjectDetail> listByVoucherId(Long voucherId);

    /**
     * 删除凭证科目明细
     *
     * @param voucherId
     * @return
     */
    boolean del(long voucherId);
}
