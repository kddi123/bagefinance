package com.bage.common.netty;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.bage.common.config.NacosConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "netty.server", name = "enable", havingValue = "true")
public class NettyServerHelper {
    final NettyConfig nettyConfig;
    //用于接收客户端的连接
    private EventLoopGroup bossGroup;
    //用于处理客户端的读写数据
    private EventLoopGroup workerGroup;
    private final NamingService namingService;
    private final NacosConfig nacosConfig;
    Instance instance = null;

    private final ObjectFactory<HeartbeatServerHandler> heartbeatServerHandlerObjectFactory;
    public final ConcurrentHashMap<String, ChannelHandlerContext> onLineClients = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    /**
     * 启动netty服务
     */
    public void run() {
        new Thread(this::startup).start();
    }

    /**
     * 添加或更新客户端信息（本地和redis）
     *
     * @param webSocketUser
     * @param ctx
     * @param updateRedisCache
     * @param memberId
     */
    public void addOrUpdateClient(WebSocketUser webSocketUser, ChannelHandlerContext ctx, boolean updateRedisCache, Long memberId) {
        boolean isOnline = onLineClients.containsKey(ctx.channel().id().asLongText());
        //本地存储channelId和ChannelHandlerContext
        onLineClients.put(ctx.channel().id().asLongText(), ctx);
        if (!isOnline) {
            saveClientInfoToRedis(webSocketUser, ctx, memberId);
        }
        if (isOnline && updateRedisCache) {
            saveClientInfoToRedis(webSocketUser, ctx, memberId);
        }
    }

    /**
     * 删除客户端（本地+缓存）
     *
     * @param ctx
     */
    public void removeClient(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        String clientId = (String) ctx.channel().attr(AttributeKey.valueOf(NettyServerConstant.CLIENT_ID)).get();
        if (Strings.isNotBlank(clientId)) {
            redisTemplate.delete(NettyServerConstant.CLIENT_ID + ":" + clientId);
        }
        onLineClients.remove(ctx.channel().id().asLongText());
    }

    private void startup() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    //使用NioSeverSocket作为通道类型
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //http解码器
                            pipeline.addLast(new HttpServerCodec());
                            //对大数据流的支持
                            pipeline.addLast(new ChunkedWriteHandler());
                            //设置单次请求的文件的大小（单位：字节）
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //添加空闲状态处理器
                            pipeline.addLast(new IdleStateHandler(10, 0, 0));
                            //添加websocket服务协议处理器，指定websocket路径
                            pipeline.addLast(new WebSocketServerProtocolHandler(nettyConfig.getWebsocketPath()));
                            //添加心跳处理器
                            pipeline.addLast(heartbeatServerHandlerObjectFactory.getObject());
                        }
                    })
                    //设置服务器等待队列大小，控制未处理的连接请求数量，超过队列数量会拒绝连接
                    .option(ChannelOption.SO_BACKLOG, nettyConfig.getQueueSize())
                    //启用tpc保活机制，确保长时间不活动的连接仍然有效
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(nettyConfig.getPort()).sync();
            log.info("netty server started on port {}", nettyConfig.getPort());
            registerService();
            //定时清理超时的用户
            startTimeOutChecker();
            //等待服务器通道关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            log.error("netty server start error", ex);
        } finally {
            shutdown();
        }
    }

    private synchronized void shutdown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (instance != null) {
            try {
                namingService.deregisterInstance(nettyConfig.getServerName(), instance.getIp(),
                        instance.getPort());
                log.info("deregistered service {} ip:{} port:{}",
                        nettyConfig.getServerName(), instance.getIp(), instance.getPort());
            } catch (Exception ex) {
                log.error("failed to deregister service", ex);
            }
        }
        log.info("WebSocket server shutdown completed.");
    }

    /**
     * 注册服务
     */
    private void registerService() {
        try {
            instance = new Instance();
            instance.setIp(InetAddress.getLocalHost().getHostAddress());
            instance.setPort(nettyConfig.getPort());
            instance.setHealthy(true);
            namingService.registerInstance(nettyConfig.getServerName(), nacosConfig.getGroup(),
                    instance);
            log.info("register service {} ip:{} port:{}", instance.getServiceName(), instance.getIp(),
                    instance.getPort());
        } catch (Exception ex) {
            log.error("register service error", ex);
        }
    }

    /**
     * 将客户端信息保存到redis中
     *
     * @param webSocketUser
     * @param ctx
     * @param memberId
     */
    private void saveClientInfoToRedis(WebSocketUser webSocketUser, ChannelHandlerContext ctx, Long memberId) {
        try {
            String key = NettyServerCacheConstant.CLIENT_ID + webSocketUser.getClientId();
            Map<String, Object> connectionInfo = new HashMap<>();
            connectionInfo.put(NettyServerConstant.CLIENT_ID, webSocketUser.getClientId());
            connectionInfo.put(NettyServerConstant.CLIENT_NAME, webSocketUser.getClientName());
            connectionInfo.put(NettyServerConstant.SERVER_ADDR, InetAddress.getLocalHost().getHostAddress() + ":" + nettyConfig.getPort());
            connectionInfo.put(NettyServerConstant.HOST, InetAddress.getLocalHost().getHostAddress());
            connectionInfo.put(NettyServerConstant.PORT, nettyConfig.getPort());
            connectionInfo.put(NettyServerConstant.CHANNEL_ID, ctx.channel().id().asLongText());
            connectionInfo.put(NettyServerConstant.TOKEN, webSocketUser.getToken());
            connectionInfo.put(NettyServerConstant.MEMBER_ID, memberId);

            //使用putAll一次性写入所有键值对
            redisTemplate.opsForHash().putAll(key, connectionInfo);
            //设置redis的过期时间
            redisTemplate.expire(key, 5L, TimeUnit.MINUTES);
        } catch (Exception ex) {
            log.error("save client info error", ex);
        }
    }

    /**
     * 从redis中获取用户连接信息
     *
     * @param clientId
     * @return
     */
    public UserConnectionInfo getClientInfoFromRedis(String clientId) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String cacheKey = NettyServerCacheConstant.CLIENT_ID + clientId;
        Map<String, Object> map = hashOperations.entries(cacheKey);

        return convertToUserConnectionInfo(map);
    }

    /**
     * 定时清理空闲超时的用户
     */
    private void startTimeOutChecker() {
        scheduled.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, ChannelHandlerContext> entry : onLineClients.entrySet()) {
                ChannelHandlerContext ctx = entry.getValue();
                LocalDateTime updateTime = (LocalDateTime) ctx.channel()
                        .attr(AttributeKey.valueOf(NettyServerConstant.UPDATE_TIME)).get();
                String clientId = (String) ctx.channel()
                        .attr(AttributeKey.valueOf(NettyServerConstant.CLIENT_ID)).get();
                //计算两个时间点之间的持续时间
                Duration duration = Duration.between(updateTime, LocalDateTime.now());
                //获取持续时间的秒数
                long seconds = duration.getSeconds();
                if (seconds > 10) {
                    removeClient(ctx);
                    log.info("remove client {}", clientId);
                }
            }
        }, 0, NettyServerConstant.TIMEOUT_THRESHOLD, TimeUnit.MINUTES);
    }

    private UserConnectionInfo convertToUserConnectionInfo(Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }
        UserConnectionInfo userConnectionInfo = new UserConnectionInfo();
        userConnectionInfo.setClientId((String) map.get(NettyServerConstant.CLIENT_ID));
        userConnectionInfo.setClientName((String) map.get(NettyServerConstant.CLIENT_NAME));
        userConnectionInfo.setHost((String) map.get(NettyServerConstant.HOST));
        Object port = map.get(NettyServerConstant.PORT);
        if (port instanceof Integer) {
            userConnectionInfo.setPort((Integer) port);
        }
        userConnectionInfo.setServerAddr((String) map.get(NettyServerConstant.SERVER_ADDR));
        userConnectionInfo.setChannelId((String) map.get(NettyServerConstant.CHANNEL_ID));
        Object memberId = map.get(NettyServerConstant.MEMBER_ID);
        if (memberId instanceof Integer) {
            userConnectionInfo.setMemberId(((Integer) memberId).longValue());
        }
        if (memberId instanceof Long) {
            userConnectionInfo.setMemberId((Long) memberId);
        }
        userConnectionInfo.setToken((String) map.get(NettyServerConstant.TOKEN));
        return userConnectionInfo;
    }
}
