package com.bage.common.netty;

import com.bage.common.dto.BaseUserInfoDTO;
import com.bage.common.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    final ObjectMapper objectMapper;
    final TokenService<BaseUserInfoDTO> tokenService;
    private final NettyServerHelper nettyServerHelper;
    //心跳次数
    private int heartbeatCount = 0;
    private int readTimeOut = 0;
    private int writeTimeOut = 0;
    private int allTimeOut = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String request = msg.text();
        log.debug("Receive heartbeat request: {}", request);
        WebSocketUser webSocketUser = objectMapper.readValue(request, WebSocketUser.class);
        if (Strings.isBlank(webSocketUser.getClientId())) {
            ctx.close();
            log.error("client id is null");
            return;
        }
        String oldClientId = (String) ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.CLIENT_ID)).get();
        String oldToken = (String) ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.TOKEN)).get();

        //将客户端信息存储在channel上
        ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.CLIENT_ID)).set(webSocketUser.getClientId());
        ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.TOKEN)).set(webSocketUser.getToken());
        ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.UPDATE_TIME)).set(LocalDateTime.now());

        boolean updateRedisCache = false;
        Long memberId = (Long) ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.MEMBER_ID)).get();
        if (!Objects.equals(oldClientId, webSocketUser.getClientId())
                || !Objects.equals(oldToken, webSocketUser.getToken())) {
            memberId = null;
        }
        if (memberId == null || memberId == 0) {
            if (Strings.isNotBlank(webSocketUser.getToken())) {
                BaseUserInfoDTO baseUserInfoDTO = tokenService.getRedisUser(webSocketUser.getToken());
                if (baseUserInfoDTO != null) {
                    memberId = baseUserInfoDTO.getId();
                    ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.MEMBER_ID)).set(memberId);
                    updateRedisCache = true;
                }
            }
        }

        if (NettyServerConstant.CMD_HEARTBEAT.equals(webSocketUser.getCmd())) {
            //用户信息保存在本地和redis中
            heartbeatCount++;
            if (heartbeatCount > 10) {
                updateRedisCache = true;
                heartbeatCount = 0;
            }
            //保存用户信息
            nettyServerHelper.addOrUpdateClient(webSocketUser, ctx, updateRedisCache, memberId);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("ok"));
            return;
        }
        //将消息传递给下一个处理器
        ctx.fireChannelRead(msg.retain());
    }

    /**
     * 当心的客户端连接时：handlerAdded 方法被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    /**
     * 当客户端断开连接时： handlerRemoved方法被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //删除本地用户和redis用户相关信息
        nettyServerHelper.removeClient(ctx);
        super.handlerRemoved(ctx);
    }

    /**
     * 当处理过程中发送异常时：execptionCaught 方法被调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //删除用户本地信息和服务端信息
        nettyServerHelper.removeClient(ctx);
        ctx.close();
        log.error("caught exception", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 处理用户超时事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String eventType = Strings.EMPTY;
        if (evt instanceof IdleStateEvent event) {
            eventType = switch (event.state()) {
                case READER_IDLE -> {
                    readTimeOut++;
                    yield "读空闲超时";
                }
                case WRITER_IDLE -> {
                    writeTimeOut++;
                    yield "写空闲超时";
                }
                case ALL_IDLE -> {
                    allTimeOut++;
                    yield "读写空闲超时";
                }
            };
            //目前只做读超时
            if (readTimeOut > 3) {
                log.info("{} readTimeOut is {}", eventType, readTimeOut);
                ctx.channel().writeAndFlush(new TextWebSocketFrame("close"));
                ctx.close();
            }
        }
    }

}
