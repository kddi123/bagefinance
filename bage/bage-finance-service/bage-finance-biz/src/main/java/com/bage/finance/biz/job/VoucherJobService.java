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
    public ReturnT<String> resetVoucherDocumentJob(String param) {
        XxlJobHelper.log("resetVoucherDocumentJob -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            voucherService.resetVoucherDocument();
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            // 不要写 ReturnT.FAIL：xxl-job BEAN 模式会丢弃返回值，必须 handleFail 才能让 admin 记为失败
            XxlJobHelper.log("任务执行失败: {}", e);
            XxlJobHelper.handleFail("resetVoucherDocumentJob 执行失败: " + e.getMessage());
            log.error("resetVoucherDocumentJob 执行失败", e);
        }
        return ReturnT.SUCCESS;
    }
}
