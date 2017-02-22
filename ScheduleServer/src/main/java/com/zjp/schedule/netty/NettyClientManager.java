package com.zjp.schedule.netty;

import com.zjp.schedule.domain.ScheduleServerProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ━━━━━━oooo━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━萌萌哒━━━━━━
 * Module Desc:com.zjp.schedule.netty
 * User: zjprevenge
 * Date: 2017/2/18
 * Time: 10:37
 */

public class NettyClientManager implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(NettyClientManager.class);

    private static final int MAX_RETRY_COUNT = 3;

    private static final int SOCKET_TIMEOUT = 5000;

    private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    private int workerGroupThreads = 16;

    private EventLoopGroup workerGroup;

    private ScheduleServerProperties scheduleServerProperties;

    public NettyClientManager() {
    }

    public NettyClientManager(ScheduleServerProperties scheduleServerProperties) {
        this.scheduleServerProperties = scheduleServerProperties;
        this.workerGroupThreads = scheduleServerProperties.getWorkerGroupThreads();
    }

    public int getWorkerGroupThreads() {
        return workerGroupThreads;
    }

    public void setWorkerGroupThreads(int workerGroupThreads) {
        this.workerGroupThreads = workerGroupThreads;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public ScheduleServerProperties getScheduleServerProperties() {
        return scheduleServerProperties;
    }

    public void setScheduleServerProperties(ScheduleServerProperties scheduleServerProperties) {
        this.scheduleServerProperties = scheduleServerProperties;
    }

    /**
     * 创建发送请求连接
     *
     * @param host 机器地址
     * @param port 端口
     * @return
     */
    public Channel connect(String host, int port) {
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringDecoder());
                        channel.pipeline().addLast(new StringEncoder());
                        channel.pipeline().addLast(new NettyClientHandler());
                    }
                });
        Channel channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
        channels.put(host + ":" + port, channel);
        return channel;
    }

    /**
     * schedule 真正执行的类
     *
     * @param host 机器地址
     * @param port 端口
     * @param key  路由地址
     * @return
     */
    public String scheduleInvoke(String host, int port, String key) {
        String hostKey = host + ":" + port;
        Channel channel = channels.get(hostKey);
        if (channel == null || !channel.isActive()) {
            channel = connect(host, port);
        }
        channel.writeAndFlush(key);
        return NettyClientHandler.getResponse(key);
    }

    /**
     * 删除指定的channel
     *
     * @param key
     */
    public void removeChannel(String key) {
        Channel channel = channels.remove(key);
        channel.close();
    }

    public void close() {
        for (Map.Entry<String, Channel> entry : channels.entrySet()) {
            entry.getValue().closeFuture().syncUninterruptibly();
        }
        channels.clear();
        channels = null;
        workerGroup.shutdownGracefully();
        workerGroup = null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("init netty manager...");
        workerGroup = new NioEventLoopGroup(workerGroupThreads);
    }

    @Override
    public void destroy() throws Exception {
        close();
    }
}
