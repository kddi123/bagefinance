package com.bage.common.sofabolt;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.bage.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

import java.net.InetSocketAddress;

@Configuration
@ConditionalOnProperty(prefix = "sofabolt", name = "enable", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class SofaboltClientConfig {
    public static final ConcurrentHashMap<String, Connection> serverConnections = new ConcurrentHashMap<>();

    @Bean
    public RpcClient rpcClient() {
        RpcClient rpcClient = null;
        try {
            rpcClient = new RpcClient();
            rpcClient.addConnectionEventProcessor(ConnectionEventType.CONNECT, (serverAddress, clientConnection) -> {
                // 保存服务端连接
                serverConnections.put(clientConnection.getChannel().id().asLongText(), clientConnection);

                //获取本地地址和端口
                InetSocketAddress localAddress = clientConnection.getLocalAddress();
                String localIp = localAddress.getAddress().getHostAddress();
                int localPort = localAddress.getPort();
                log.info("【与服务端-连接成功】：{}，服务端IP：{}，客户端本地IP和端口：{}:{}", clientConnection.getChannel().id().asLongText(),
                        serverAddress, localIp, localPort);

            });

            rpcClient.addConnectionEventProcessor(ConnectionEventType.CLOSE, (serverAddress, clientConnection) -> {
                // 保存服务端连接
                serverConnections.remove(clientConnection.getChannel().id().asLongText());

                //获取本地地址和端口
                InetSocketAddress localAddress = clientConnection.getLocalAddress();
                String localIp = localAddress.getAddress().getHostAddress();
                int localPort = localAddress.getPort();
                log.info("【与服务端-连接断开】：{}，服务端IP：{}，客户端本地IP和端口：{}:{}", clientConnection.getChannel().id().asLongText(),
                        serverAddress, localIp, localPort);

            });

            rpcClient.registerUserProcessor(new SyncUserProcessor<SofaboltMsgRequest>() {

                @Override
                public Object handleRequest(BizContext bizContext, SofaboltMsgRequest sofaboltMsgRequest) throws Exception {
                    log.info("【收到服务器消息】：{}，消息：{}", bizContext.getConnection().getChannel().id().asLongText(),
                            sofaboltMsgRequest.toString());
                    return new SofaboltMsgResponse("Hello " + bizContext.getConnection().getChannel().id().asLongText());
                }

                @Override
                public String interest() {
                    return SofaboltMsgRequest.class.getName();
                }
            });
            rpcClient.startup();
            log.info("Sofabolt Client started");
        } catch (Exception e) {
            throw new BizException("Failed to start sofabolt client", e);
        }
        return rpcClient;
    }
}
