package com.bage.common.sofabolt;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.bage.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConditionalOnProperty(prefix = "sofabolt", name = "enable", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SofaboltServerConfig {
    private final SofaboltConfig sofaboltConfig;
    public static final ConcurrentHashMap<String, Connection> clientConnections = new ConcurrentHashMap<>();
    final NamingService namingService;

    @Bean
    public RpcServer rpcServer() {
        RpcServer rpcServer = null;
        try {
            //创建rpc服务端
            rpcServer = new RpcServer(sofaboltConfig.getServerPort());
            rpcServer.addConnectionEventProcessor(ConnectionEventType.CONNECT, (clientAddress, serverConnection) -> {
                //保存客户端连接
                clientConnections.put(serverConnection.getChannel().id().asLongText(), serverConnection);
                //获取本地地址和端口
                InetSocketAddress localAddress = serverConnection.getLocalAddress();
                String localIp = localAddress.getAddress().getHostAddress();
                int localPort = localAddress.getPort();
                log.info("【与客户端-连接成功】：{}，客户端IP：{}，服务端本地IP和端口：{}:{}", serverConnection.getChannel().id().asLongText(),
                        clientAddress, localIp, localPort);
            });

            rpcServer.addConnectionEventProcessor(ConnectionEventType.CLOSE, (clientAddress, serverConnection) -> {
                //删除客户端连接
                clientConnections.remove(serverConnection.getChannel().id().asLongText());
                //获取本地地址和端口
                InetSocketAddress localAddress = serverConnection.getLocalAddress();
                String localIp = localAddress.getAddress().getHostAddress();
                int localPort = localAddress.getPort();
                log.info("【与客户端-连接断开】：{}，客户端IP：{}，服务端本地IP和端口：{}:{}", serverConnection.getChannel().id().asLongText(),
                        clientAddress, localIp, localPort);
            });

            rpcServer.registerUserProcessor(new SyncUserProcessor<SofaboltMsgRequest>() {
                @Override
                public Object handleRequest(BizContext bizContext, SofaboltMsgRequest sofaboltMsgRequest) throws Exception {
                    log.info("【收到客户端消息】：{}，消息：{}", bizContext.getConnection().getChannel().id().asLongText(),
                            sofaboltMsgRequest.toString());
                    return new SofaboltMsgResponse("Hello " + bizContext.getConnection().getChannel().id().asLongText());
                }

                @Override
                public String interest() {
                    return SofaboltMsgRequest.class.getName();
                }
            });
            //启动服务器
            rpcServer.startup();
                log.info("Sofabolt Server started on port {}", sofaboltConfig.getServerPort());
            registerService();
            return rpcServer;
        } catch (Exception e) {
            throw new BizException("Failed to start sofabolt server", e);
        }
    }

    private void registerService() throws UnknownHostException, NacosException {
        Instance instance = new Instance();
        instance.setIp(InetAddress.getLocalHost().getHostAddress());
        instance.setPort(sofaboltConfig.getServerPort());
        instance.setHealthy(true);
        namingService.registerInstance(sofaboltConfig.getServerName(), sofaboltConfig.getServerGroup(), instance);
    }
}
