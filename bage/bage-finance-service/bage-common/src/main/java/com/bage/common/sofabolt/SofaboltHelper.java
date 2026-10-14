package com.bage.common.sofabolt;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.bage.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "sofabolt", name = "enable", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SofaboltHelper {
    private final NamingService namingService;
    private final SofaboltConfig sofaboltConfig;

    /**
     * 通过ip从nacos服务中获取实例
     *
     * @param ip
     * @return
     */
    public Instance getInstance(String ip) {
        try {
            List<Instance> healthyInstance = namingService.getAllInstances(sofaboltConfig.getServerName(),
                    sofaboltConfig.getServerGroup(), true);
            for (Instance instance : healthyInstance) {
                if (instance.getIp().equals(ip)
                        && instance.getPort() == sofaboltConfig.getServerPort()) {
                    return instance;
                }
            }
            return null;
        } catch (Exception ex) {
            throw new BizException("getInstance", ex);
        }
    }
}
