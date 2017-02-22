package com.zjp.schedule.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

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

public class NettyClientHandler extends ChannelHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    private final static Map<String, BlockingQueue<String>> responseMap = new ConcurrentHashMap<String, BlockingQueue<String>>();

    public NettyClientHandler() {
    }

    /**
     * 真正发送发送请求
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String request = (String) msg;
        responseMap.putIfAbsent(request, new LinkedBlockingQueue<String>(1));
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 获取相应的位置
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String response = (String) msg;
        log.info("response:{}", response);
        String[] split = response.split("\\{");
        BlockingQueue<String> queue = responseMap.get(split[0]);
        queue.add("{" + split[1]);
    }

    /**
     * 获取响应消息
     *
     * @param requestId 请求id
     * @return
     */
    public static String getResponse(String requestId) {
        String response = null;
        responseMap.putIfAbsent(requestId, new LinkedBlockingQueue<String>(1));
        try {
            response = responseMap.get(requestId).take();
        } catch (InterruptedException e) {
            log.error("get response error:{}", e);
            throw new RuntimeException(e);
        } finally {
            responseMap.remove(requestId);
        }
        return response;
    }
}
