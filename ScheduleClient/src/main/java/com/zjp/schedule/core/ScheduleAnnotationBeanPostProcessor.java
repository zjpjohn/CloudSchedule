package com.zjp.schedule.core;

import com.zjp.schedule.annotation.QSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
 * Time: 18:11
 */
@Component
public class ScheduleAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ScheduleAnnotationBeanPostProcessor.class);

    private final Set<Object> annotatedBean = new HashSet<Object>();


    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        Class<?> clazz = bean.getClass();
        QSchedule qSchedule = clazz.getAnnotation(QSchedule.class);
        if (!annotatedBean.contains(bean)) {
            if (qSchedule != null) {
                annotatedBean.add(bean);
            }
        }
        return bean;
    }

    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public Set<Object> getAnnotatedBean() {
        return annotatedBean;
    }
}
