package com.zjp.schedule.config;

import com.zjp.schedule.bean.QScheduleProperties;
import com.zjp.schedule.core.ScheduleEntrypointRegistry;
import com.zjp.schedule.curator.CuratorClientManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

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
 * Date: 2017/2/17
 * Time: 18:18
 */
@Configuration
public class ScheduleBeanConfiguration {

    @Resource
    private QScheduleProperties qScheduleProperties;

//    @Bean
//    public ScheduledAnnotationBeanPostProcessor beanPostProcessor() {
//        return new ScheduledAnnotationBeanPostProcessor();
//    }

    @Bean
    public ScheduleEntrypointRegistry scheduleEntrypointRegistry() {
        return new ScheduleEntrypointRegistry();
    }

    @Bean
    public CuratorClientManager curatorClientManager() {
        return new CuratorClientManager(qScheduleProperties);
    }
}
