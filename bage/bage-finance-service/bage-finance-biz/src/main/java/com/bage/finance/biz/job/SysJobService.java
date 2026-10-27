package com.bage.finance.biz.job;

import com.bage.finance.biz.service.SysRoleService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class SysJobService {
    final SysRoleService sysRoleService;

    /**
     * 将所有角色绑定的菜单设置到缓存中
     */
    @XxlJob("setSysRoleMenuCacheJobHandler")
    public ReturnT<String> setSysRoleMenuCacheJobHandler(String param) {
        XxlJobHelper.log("setSysRoleMenuCacheJobHandler -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            sysRoleService.setSysRoleMenuCache();
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            // 不要写 ReturnT.FAIL：xxl-job BEAN 模式会丢弃返回值，必须 handleFail 才能让 admin 记为失败
            XxlJobHelper.log("任务执行失败: {}", e);
            XxlJobHelper.handleFail("setSysRoleMenuCacheJobHandler 执行失败: " + e.getMessage());
            log.error("setSysRoleMenuCacheJobHandler 执行失败", e);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 将所有角色绑定的资源设置到缓存中
     */
    @XxlJob("setSysRoleResourceCacheJobHandler")
    public ReturnT<String> setSysRoleResourceCacheJobHandler(String param) {
        XxlJobHelper.log("setSysRoleResourceCacheJobHandler -> begin");
        try {
            XxlJobHelper.log("开始执行任务");
            sysRoleService.setSysRoleResourceCache();
            XxlJobHelper.log("任务执行结束");
        } catch (Exception e) {
            // 不要写 ReturnT.FAIL：xxl-job BEAN 模式会丢弃返回值，必须 handleFail 才能让 admin 记为失败
            XxlJobHelper.log("任务执行失败: {}", e);
            XxlJobHelper.handleFail("setSysRoleResourceCacheJobHandler 执行失败: " + e.getMessage());
            log.error("setSysRoleResourceCacheJobHandler 执行失败", e);
        }
        return ReturnT.SUCCESS;
    }
}
