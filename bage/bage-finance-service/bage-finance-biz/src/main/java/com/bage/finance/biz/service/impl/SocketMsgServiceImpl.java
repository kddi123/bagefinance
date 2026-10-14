package com.bage.finance.biz.service.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alipay.remoting.rpc.RpcClient;
import com.bage.common.exception.BizException;
import com.bage.common.netty.NettyServerHelper;
import com.bage.common.netty.UserConnectionInfo;
import com.bage.common.sofabolt.SofaboltConfig;
import com.bage.common.sofabolt.SofaboltHelper;
import com.bage.common.sofabolt.SofaboltMsgRequest;
import com.bage.common.sofabolt.SofaboltMsgResponse;
import com.bage.finance.biz.service.SocketMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketMsgServiceImpl<T extends SofaboltMsgRequest> implements SocketMsgService<T> {
    final NettyServerHelper nettyServerHelper;
    final RpcClient rpcClient;
    final SofaboltConfig sofaboltConfig;
    final SofaboltHelper sofaboltHelper;


    /**
     * 发送sofabolt消息
     *
     * @param request
     */
    @Override
    public void sendMsgByClientId(T request) {
        UserConnectionInfo userConnectionInfo = nettyServerHelper.getClientInfoFromRedis(request.getClientId());
        if (userConnectionInfo == null) {
            throw new BizException("用户已下线：" + request.getClientId());
        }
        request.setChannelId(userConnectionInfo.getChannelId());
        try {
            Instance instance = sofaboltHelper.getInstance(userConnectionInfo.getHost());
            if (instance == null) {
                throw new BizException("服务器：" + userConnectionInfo.getHost() + "，已下线");
            }
            String address = instance.getIp() + ":" + instance.getPort();
            SofaboltMsgResponse response = (SofaboltMsgResponse) rpcClient.invokeSync(address, request, 3000);
            log.info("Response from {}:{}", request.getServerAddress(), response);
        } catch (Exception ex) {
            throw new BizException("发送消息失败", ex);
        }
    }
}
