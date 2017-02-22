package com.zjp.schedule.config;

import com.zjp.schedule.bean.QScheduleProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
 * Module Desc:com.zjp.schedule
 * User: zjprevenge
 * Date: 2017/2/20
 * Time: 15:20
 */
@Configuration
public class ScheduleConfig {

    @Resource
    private Environment env;

    @Bean
    public QScheduleProperties qScheduleProperties() {
        QScheduleProperties properties = new QScheduleProperties();
        properties.setAppName(env.getProperty("schedule.appName"));
        properties.setEnv(env.getProperty("schedule.env"));
        properties.setNameSpace(env.getProperty("schedule.nameSpace"));
        properties.setPort(Integer.valueOf(env.getProperty("schedule.port")));
        properties.setUrl(env.getProperty("schedule.url"));
        return properties;
    }
}
