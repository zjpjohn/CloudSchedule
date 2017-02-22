package com.zjp.schedule.config;

import com.zjp.schedule.curator.CuratorClientManager;
import com.zjp.schedule.discovery.ScheduleDiscovery;
import com.zjp.schedule.domain.ScheduleServerProperties;
import com.zjp.schedule.netty.NettyClientManager;
import com.zjp.schedule.schedule.ScheduleServerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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
 * Module Desc:com.zjp.schedule.config
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 11:42
 */
@Configuration
public class ScheduleServerConfig {

    @Resource
    private ScheduleServerProperties scheduleServerProperties;

    @Bean
    public NettyClientManager nettyClientManager() {
        return new NettyClientManager(scheduleServerProperties);
    }

    @Bean
    public CuratorClientManager curatorClientManager() {
        return new CuratorClientManager(scheduleServerProperties);
    }

    @Bean
    public ScheduleServerContainer scheduleServerContainer() {
        return new ScheduleServerContainer();
    }

    @Bean
    public ScheduleDiscovery scheduleDiscovery() {
        return new ScheduleDiscovery(curatorClientManager(),
                scheduleServerContainer(),
                nettyClientManager());
    }
}
