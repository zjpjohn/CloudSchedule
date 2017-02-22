package com.zjp.schedule.core;

import com.zjp.schedule.annotation.QSchedule;
import com.zjp.schedule.annotation.Schedule;
import com.zjp.schedule.bean.QScheduleProperties;
import com.zjp.schedule.bean.ScheduleData;
import com.zjp.schedule.bean.ScheduleInvokeHandler;
import com.zjp.schedule.curator.CuratorClientManager;
import com.zjp.schedule.netty.NettyServer;
import com.zjp.schedule.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetAddress;
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
 * Module Desc:com.zjp.schedule.core
 * User: zjprevenge
 * Date: 2017/2/17
 * Time: 18:16
 */

public class ScheduleEntrypointRegistry implements DisposableBean, InitializingBean, SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEntrypointRegistry.class);

    private static final String SEPARATOR = "/";

    private static final String TASK = "/task";

    @Resource
    private QScheduleProperties qScheduleProperties;

    @Resource
    private CuratorClientManager curatorClientManager;

    @Resource
    private ScheduleAnnotationBeanPostProcessor scheduleAnnotationBeanPostProcessor;

    private String schedulePath;

    private final Map<String, ScheduleInvokeHandler> scheduleInvokeHandlers = new ConcurrentHashMap<String, ScheduleInvokeHandler>();

    //获取机器的ip
    private String host;

    private NettyServer nettyServer;

    private RegistryInvokeTask registryInvokeTask;

    public ScheduleInvokeHandler getScheduleHandler(String key) {
        return scheduleInvokeHandlers.get(key);
    }

    public void registryScheduleHandler(String key, String cron, ScheduleInvokeHandler invokeHandler) throws Exception {
        scheduleInvokeHandlers.put(key, invokeHandler);
        schedulePath = qScheduleProperties.getAppName() + ":" + qScheduleProperties.getEnv();
        host = InetAddress.getLocalHost().getHostAddress();
        String appPath = new StringBuilder(TASK)
                .append(SEPARATOR)
                .append(schedulePath)
                .toString();
        //添加应用数据
        curatorClientManager.addData(appPath, schedulePath, CreateMode.PERSISTENT);
        //注册定时任务信息
        String dataPath = new StringBuilder(TASK)
                .append(SEPARATOR)
                .append(schedulePath)
                .append(SEPARATOR)
                .append(CuratorClientManager.DATA_PATH)
                .append(SEPARATOR)
                .append(key)
                .toString();
        //注册数据信息
        ScheduleData scheduleData = new ScheduleData(key,
                cron,
                qScheduleProperties.getAppName(),
                qScheduleProperties.getEnv(),
                dataPath);
        //添加调度数据
        curatorClientManager.addData(dataPath, JsonUtils.bean2Json(scheduleData), CreateMode.PERSISTENT);
        //注册机器信息
        String machinePath = new StringBuilder(TASK)
                .append(SEPARATOR)
                .append(schedulePath)
                .append(SEPARATOR)
                .append(CuratorClientManager.MACHINE_PATH)
                .append(SEPARATOR)
                .append(host + ":" + qScheduleProperties.getPort())
                .toString();
        curatorClientManager.addData(machinePath, host + ":" + qScheduleProperties.getPort(), CreateMode.EPHEMERAL);
    }

    public void destroy() throws Exception {
        scheduleInvokeHandlers.clear();
        curatorClientManager.stop();
    }

    /**
     * 注册job信息
     *
     * @param qSchedule
     * @param schedule
     * @param bean
     * @param method
     * @param args
     * @throws Exception
     */
    public void registry(QSchedule qSchedule,
                         Schedule schedule,
                         Object bean,
                         Method method,
                         Object[] args) throws Exception {

        StringBuilder builder = new StringBuilder();
        String key = builder.append(StringUtils.isNoneBlank(qSchedule.value()) ? qSchedule.value() : bean.getClass().getSimpleName())
                .append(":")
                .append(StringUtils.isNoneBlank(schedule.value()) ? schedule.value() : method.getName()).toString();
        ScheduleInvokeHandler invokeHandler = new ScheduleInvokeHandler(bean, method, args);
        registryScheduleHandler(key, schedule.cron(), invokeHandler);
    }

    @Override
    public void afterSingletonsInstantiated() {
        //开启服务器
        nettyServer.startUp();
        //注册job信息至zookeeper
        registryInvokeTask.start();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        nettyServer = new NettyServer(this, qScheduleProperties);
        registryInvokeTask = new RegistryInvokeTask(this, CuratorClientManager.LATCH, scheduleAnnotationBeanPostProcessor);
    }
}
