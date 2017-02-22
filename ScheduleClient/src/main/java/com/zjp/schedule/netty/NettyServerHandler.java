package com.zjp.schedule.netty;

import com.zjp.schedule.bean.ScheduleInvokeHandler;
import com.zjp.schedule.core.ScheduleEntrypointRegistry;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

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
 * Time: 18:50
 */

public class NettyServerHandler extends ChannelHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private ScheduleEntrypointRegistry entrypointRegistry;

    private ExecutorService executor;

    public NettyServerHandler() {
    }

    public NettyServerHandler(ExecutorService executor, ScheduleEntrypointRegistry entrypointRegistry) {
        this.entrypointRegistry = entrypointRegistry;
        this.executor = executor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final String request = (String) msg;
        if (StringUtils.isNotEmpty(request)) {
            final ScheduleInvokeHandler scheduleHandler = entrypointRegistry.getScheduleHandler(request);
            if (scheduleHandler != null) {
                executor.submit(scheduleHandler);
                String result = request + "{" + scheduleHandler.getBean().getClass().getSimpleName() + "." + scheduleHandler.getMethod().getName() + "}";
                ctx.writeAndFlush(result);
            }
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught:{}", cause);
        ctx.close();
    }
}
