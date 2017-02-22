package com.zjp.schedule.core;

import com.zjp.schedule.annotation.QSchedule;
import com.zjp.schedule.annotation.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
 * Date: 2017/2/20
 * Time: 18:59
 */

public class RegistryInvokeTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RegistryInvokeTask.class);

    private ScheduleEntrypointRegistry scheduleEntrypointRegistry;

    private CountDownLatch countDownLatch;

    private ScheduleAnnotationBeanPostProcessor scheduleAnnotationBeanPostProcessor;

    public RegistryInvokeTask() {
    }

    public RegistryInvokeTask(ScheduleEntrypointRegistry scheduleEntrypointRegistry,
                              CountDownLatch countDownLatch,
                              ScheduleAnnotationBeanPostProcessor scheduleAnnotationBeanPostProcessor) {
        this.scheduleEntrypointRegistry = scheduleEntrypointRegistry;
        this.countDownLatch = countDownLatch;
        this.scheduleAnnotationBeanPostProcessor = scheduleAnnotationBeanPostProcessor;
    }

    public ScheduleEntrypointRegistry getScheduleEntrypointRegistry() {
        return scheduleEntrypointRegistry;
    }

    public void setScheduleEntrypointRegistry(ScheduleEntrypointRegistry scheduleEntrypointRegistry) {
        this.scheduleEntrypointRegistry = scheduleEntrypointRegistry;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public ScheduleAnnotationBeanPostProcessor getScheduleAnnotationBeanPostProcessor() {
        return scheduleAnnotationBeanPostProcessor;
    }

    public void setScheduleAnnotationBeanPostProcessor(ScheduleAnnotationBeanPostProcessor scheduleAnnotationBeanPostProcessor) {
        this.scheduleAnnotationBeanPostProcessor = scheduleAnnotationBeanPostProcessor;
    }

    public void start() {
        new Thread(this, "registry invoke task").start();
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();
            log.info("registry invoker to zookeeper...");
            Set<Object> annotatedBean = scheduleAnnotationBeanPostProcessor.getAnnotatedBean();
            for (Object bean : annotatedBean) {
                Class<?> clazz = bean.getClass();
                QSchedule qSchedule = clazz.getAnnotation(QSchedule.class);
                for (Method method : clazz.getDeclaredMethods()) {
                    Schedule schedule = method.getAnnotation(Schedule.class);
                    //方法上标注注解
                    if (schedule != null && method.getParameterTypes().length == 0) {
                        try {
                            scheduleEntrypointRegistry.registry(qSchedule, schedule, bean, method, null);
                        } catch (Exception e) {
                            log.error("registry schedule error:{}", e);
                            continue;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("registry invoke error:{}", e);
        }
    }
}
