package com.bage.finance.biz.config;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.bage.common.exception.BizException;
import com.bage.common.netty.NettyServerHelper;
import com.bage.common.sofabolt.SofaboltMsgResponse;
import com.bage.finance.biz.dto.form.msg.ScanSucMsgForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketServerInitConfig implements CommandLineRunner {
    final NettyServerHelper nettyServerHelper;
    final RpcServer rpcServer;
    final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        //注册处理器
        rpcServer.registerUserProcessor(new SyncUserProcessor<ScanSucMsgForm>() {
            @Override
            public Object handleRequest(BizContext bizContext, ScanSucMsgForm scanSucMsgForm) throws Exception {
                log.info("【收到客户消息】:{}，消息：{}", bizContext.getConnection().getChannel().id().asLongText(),
                        scanSucMsgForm.toString());
                ChannelHandlerContext ctx = nettyServerHelper.onLineClients.get(scanSucMsgForm.getChannelId());
                if (ctx == null) {
                    throw new BizException("用户已下线：" + scanSucMsgForm.toString());
                }
                ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(scanSucMsgForm)));
                //返回响应
                return new SofaboltMsgResponse("消息已推送给前端");
            }

            @Override
            public String interest() {
                return ScanSucMsgForm.class.getName();
            }
        });
    }
}
