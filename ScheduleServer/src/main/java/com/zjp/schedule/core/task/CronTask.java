package com.zjp.schedule.core.task;

import com.zjp.schedule.core.job.Job;
import com.zjp.schedule.core.trigger.CronTrigger;
import com.zjp.schedule.core.trigger.Trigger;

import java.util.concurrent.ScheduledFuture;

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
 * Module Desc:com.zjp.schedule.core.task
 * User: zjprevenge
 * Date: 2017/2/21
 * Time: 22:11
 */

public class CronTask {

    public enum Status {
        NEW, RUNNING, STOP;
    }

    //任务分组
    private String groupName;

    //任务唯一标示
    private String key;

    //任务执行表达式
    private String expression;

    //任务触发器
    private Trigger trigger;

    //执行的任务
    private Job job;

    //任务执行回调
    private ScheduledFuture<?> scheduledFuture;

    //任务所属分组
    private ScheduleTaskGroup taskGroup;

    //任务状态
    private Status status = Status.NEW;

    public CronTask() {
    }

    public CronTask(String key, String expression, Job job) {
        this.key = key;
        this.expression = expression;
        this.job = job;
        this.trigger = new CronTrigger(expression);
    }

    /**
     * 取消任务
     *
     * @return
     */
    public boolean cancelTask() {
        if (status == Status.NEW || status == Status.STOP) {
            return false;
        }
        return scheduledFuture.cancel(true);
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public ScheduleTaskGroup getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(ScheduleTaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
