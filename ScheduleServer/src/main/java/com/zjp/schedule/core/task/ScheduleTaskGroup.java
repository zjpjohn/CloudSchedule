package com.zjp.schedule.core.task;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zjp.schedule.core.job.Job;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

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
 * Time: 22:48
 */

public class ScheduleTaskGroup {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskGroup.class);

    public static final String DEFAULT = "default_group";

    //状态
    public enum Status {
        READY, RUNNING, STOP
    }

    //容器总运行的任务数量
    private AtomicInteger runningCount = new AtomicInteger(0);

    //容器状态
    private Status status = Status.RUNNING;

    /**
     * 容器名称
     * 容器设计按应用进行分组
     * 一个应用对应一个容器
     */
    private String groupName = DEFAULT;

    /**
     * 最大任务数量
     */
    private int maxTask = 100;
    /**
     * 任务列表
     * 容器持有的任务列表
     */
    private Map<String, CronTask> tasks = Maps.newConcurrentMap();

    /**
     * 机器列表
     * 定时任务真正要执行的机器
     */
    private List<String> machines = new CopyOnWriteArrayList<String>();

    /**
     * 调度器
     */
    private ThreadPoolTaskSchedule schedule;

    public ScheduleTaskGroup(String groupName) {
        this(groupName, 16);
    }

    public ScheduleTaskGroup(String groupName, int maxTask) {
        this.groupName = groupName;
        this.maxTask = maxTask;
        this.schedule = new ThreadPoolTaskSchedule(maxTask, groupName);
    }

    /**
     * 添加机器
     *
     * @param machine
     * @return
     */
    public boolean addMachines(String machine) {
        if (!machines.contains(machine)) {
            machines.add(machine);
        }
        return false;
    }

    public List<String> getMachines() {
        return machines;
    }

    /**
     * 删除执行机器
     *
     * @param machine
     * @return
     */
    public boolean removeMachine(String machine) {
        boolean result = machines.remove(machine);
        //当已经没有可以执行的机器，容器处于待准备状态，等待机器上线
        if (machines.size() < 1) {
            this.status = Status.READY;
        }
        return result;
    }

    /**
     * 加载全部机器列表
     *
     * @param machines
     * @return
     */
    public boolean addMachines(List<String> machines) {
        return this.machines.addAll(machines);
    }

    /**
     * 获取全部的任务集合
     *
     * @return
     */
    public Collection<CronTask> getTasks() {
        return tasks.values();
    }

    /**
     * 获取指定的任务
     *
     * @param taskName 任务名称
     * @return
     */
    public CronTask getTask(String taskName) {
        return tasks.get(taskName);
    }

    /**
     * 启动全部任务
     *
     * @throws Exception
     */
    public void startAll() throws Exception {
        for (CronTask cronTask : tasks.values()) {
            startTask(cronTask);
        }
    }

    /**
     * 启动容器中指定名称的任务
     *
     * @param taskName
     * @return
     * @throws Exception
     */
    public boolean startTask(String taskName) throws Exception {
        CronTask cronTask = tasks.get(taskName);
        if (cronTask != null) {
            return startTask(cronTask);
        }
        return false;
    }

    /**
     * 添加任务
     *
     * @param cronTask
     */
    public void addTask(CronTask cronTask) throws Exception {
        Preconditions.checkNotNull(cronTask, "task must not be null");
        //设置任务所属分组名称
        cronTask.setGroupName(groupName);
        String key = cronTask.getKey();
        Job job = cronTask.getJob();
        if (StringUtils.isEmpty(key)) {
            cronTask.setKey(StringUtils.isNotEmpty(job.getName()) ? job.getName() : job.toString());
        }
        if (tasks.containsKey(key)) {
            tasks.remove(key);
        }
        cronTask.setTaskGroup(this);
        if (this.status == Status.READY
                || this.status == Status.STOP) {
            this.status = Status.RUNNING;
        }
        if (this.status == Status.RUNNING) {
            startTask(cronTask);
        }
    }

    /**
     * 启动指定任务
     *
     * @param cronTask
     */
    public boolean startTask(CronTask cronTask) throws Exception {
        Preconditions.checkNotNull(cronTask);
        //状态处于new 或 stop可以进行启动，
        if (cronTask.getStatus() == CronTask.Status.NEW
                || cronTask.getStatus() == CronTask.Status.STOP) {
            //执行任务
            ScheduledFuture<?> scheduledFuture = this.schedule.schedule(cronTask);
            log.info(cronTask.getKey() + "<-->" + cronTask.getExpression());
            cronTask.setStatus(CronTask.Status.RUNNING);
            cronTask.setScheduledFuture(scheduledFuture);
            tasks.put(cronTask.getKey(), cronTask);
            runningCount.incrementAndGet();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 暂停全部任务
     *
     * @throws Exception
     */
    public void pauseAll() throws Exception {
        Set<String> taskNames = this.tasks.keySet();
        for (String taskName : taskNames) {
            pause(taskName);
        }
        this.status = Status.STOP;
    }

    /**
     * 暂停指定任务
     *
     * @param taskName 任务名称
     * @return
     * @throws Exception
     */
    public boolean pause(String taskName) throws Exception {
        CronTask cronTask = tasks.get(taskName);
        if (cronTask != null
                && cronTask.getStatus() == CronTask.Status.RUNNING) {
            cronTask.setStatus(CronTask.Status.STOP);
            runningCount.decrementAndGet();
            return cronTask.cancelTask();
        }
        return false;
    }

    /**
     * 删除容器中的任务
     *
     * @throws Exception
     */
    public void removeAll() throws Exception {
        Set<String> taskNames = tasks.keySet();
        for (String taskName : taskNames) {
            remove(taskName);
        }
    }

    /**
     * 删除指定任务
     * 先停止任务，再从容器中删除
     *
     * @param taskName
     * @return
     * @throws Exception
     */
    public CronTask remove(String taskName) throws Exception {
        CronTask cronTask = tasks.get(taskName);
        if (cronTask != null) {
            //处于运行状态，先取消任务
            if (cronTask.getStatus() == CronTask.Status.RUNNING) {
                cronTask.cancelTask();
            }
            tasks.remove(taskName);
            runningCount.decrementAndGet();
            return cronTask;
        }
        return null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getMaxTask() {
        return maxTask;
    }

    public void setMaxTask(int maxTask) {
        this.maxTask = maxTask;
    }

    public void setTasks(Map<String, CronTask> tasks) {
        this.tasks = tasks;
    }

    public void setMachines(List<String> machines) {
        this.machines = machines;
    }
}
