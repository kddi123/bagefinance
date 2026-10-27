package com.bage.finance.biz.job;

import com.bage.wx.config.WxConfig;
import com.bage.wx.service.WXService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class WxJobService {
    final WXService wxService;
    final WxConfig wxConfig;

    /**
     * 设置公众号token缓存
     */
    @XxlJob("setMpAccessTokenCacheJobHandler")
    public ReturnT<String> setMpAccessTokenCacheJobHandler(String param) {
        XxlJobHelper.log("setMpAccessTokenCacheJobHandler -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            wxService.setMpAccessTokenCache(wxConfig.getMp().getAppId(), wxConfig.getMp().getSecret());
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            // 不要写 ReturnT.FAIL：xxl-job BEAN 模式会丢弃返回值，必须 handleFail 才能让 admin 记为失败
            XxlJobHelper.log("任务执行失败: {}", e);
            XxlJobHelper.handleFail("setMpAccessTokenCacheJobHandler 执行失败: " + e.getMessage());
            log.error("setMpAccessTokenCacheJobHandler 执行失败", e);
        }
        return ReturnT.SUCCESS;
    }
}