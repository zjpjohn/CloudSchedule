package com.zjp.schedule.netty;

import com.zjp.schedule.bean.QScheduleProperties;
import com.zjp.schedule.core.ScheduleEntrypointRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
 * Date: 2017/2/17
 * Time: 18:48
 */

public class NettyServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ScheduleEntrypointRegistry entrypointRegistry;

    private QScheduleProperties qScheduleProperties;

    private ExecutorService businessExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

    /**
     * 启动server
     */
    public void startUp() {
        new Thread(this, "netty server start...").start();
    }

    public void stop() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("Close netty server error:{}", e);
        }
    }

    public NettyServer() {
    }


    public NettyServer(ScheduleEntrypointRegistry entrypointRegistry, QScheduleProperties qScheduleProperties) {
        this.entrypointRegistry = entrypointRegistry;
        this.qScheduleProperties = qScheduleProperties;
    }

    public QScheduleProperties getqScheduleProperties() {
        return qScheduleProperties;
    }

    public void setqScheduleProperties(QScheduleProperties qScheduleProperties) {
        this.qScheduleProperties = qScheduleProperties;
    }

    public ScheduleEntrypointRegistry getEntrypointRegistry() {
        return entrypointRegistry;
    }

    public void setEntrypointRegistry(ScheduleEntrypointRegistry entrypointRegistry) {
        this.entrypointRegistry = entrypointRegistry;
    }


    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new StringDecoder());
                        channel.pipeline().addLast(new StringEncoder());
                        channel.pipeline().addLast(new NettyServerHandler(businessExecutor, entrypointRegistry));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture channelFuture = bootstrap.bind(qScheduleProperties.getPort()).sync();
            log.info("Start the netty server on port:{}", qScheduleProperties.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.error("Start the server on port error:{}", qScheduleProperties.getPort(), e);
        }
    }
}
