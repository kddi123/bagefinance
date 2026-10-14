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
@AllArgsConstructor
public class WxJobService {
    final WXService wxService;
    final WxConfig wxConfig;

    /**
     * 设置公众号token缓存
     */
    @XxlJob("setMpAccessTokenCacheJobHandler")
    public ReturnT<String> setMpAccessTokenCacheJobHandler(String param) throws Exception {
        XxlJobHelper.log("setBrandConfigCacheJobHandler -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            wxService.setMpAccessTokenCache(wxConfig.getMp().getAppId(), wxConfig.getMp().getSecret());
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            XxlJobHelper.log("任务执行失败", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}