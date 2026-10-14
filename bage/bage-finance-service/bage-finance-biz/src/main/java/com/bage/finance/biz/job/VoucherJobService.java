package com.bage.finance.biz.job;

import com.bage.finance.biz.service.VoucherService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class VoucherJobService {
    final VoucherService voucherService;

    /**
     * 刷新凭证数据到es中
     */
    @XxlJob("resetVoucherDocumentJob")
    public ReturnT<String> resetVoucherDocumentJob(String param) throws Exception {
        XxlJobHelper.log("resetVoucherDocumentJob -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            voucherService.resetVoucherDocument();
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            XxlJobHelper.log("任务执行失败", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
