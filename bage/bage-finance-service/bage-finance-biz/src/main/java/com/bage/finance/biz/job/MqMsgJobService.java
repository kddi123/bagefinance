package com.bage.finance.biz.job;

import com.bage.finance.biz.service.MqMsgService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class MqMsgJobService {
    final MqMsgService mqMsgService;

    /**
     * 处理定时MQ消息
     */
    @XxlJob("handleMqMessage")
    public ReturnT<String> handleMqMessage(String param) throws Exception {
        XxlJobHelper.log("handleMqMessage -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            mqMsgService.handleMqMessage();
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            XxlJobHelper.log("任务执行失败", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
