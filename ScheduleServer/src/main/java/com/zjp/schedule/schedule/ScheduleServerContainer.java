package com.zjp.schedule.schedule;

import com.zjp.schedule.core.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

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
 * Module Desc:com.zjp.schedule.schedule
 * User: zjprevenge
 * Date: 2017/2/18
 * Time: 20:51
 */

public class ScheduleServerContainer implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ScheduleServerContainer.class);

    //默认容器名称
    private static final String DEFAULT_CONTAINER_NAME = "schedule-server";
    //容器名称
    private String containerName = DEFAULT_CONTAINER_NAME;
    //任务执行容器
    private ScheduleTaskContainer taskContainer;

    public ScheduleServerContainer() {
        taskContainer = new ScheduleTaskContainer(this.containerName);
    }

    public ScheduleServerContainer(String containerName) {
        this.containerName = containerName;
        taskContainer = new ScheduleTaskContainer(this.containerName);
    }

    /**
     * 添加任务分组，实际按应用进行分组
     *
     * @param groupName
     */
    public void addTaskGroup(String groupName) {
        taskContainer.addGroup(groupName);
    }

    /**
     * 添加可执行任务
     *
     * @param groupName 任务分组名称
     * @param task      任务
     */
    public void addTask(String groupName, CronTask task) throws Exception {
        taskContainer.addTask(task, groupName);
    }

    /**
     * 判断是否存在任务
     *
     * @param groupName
     * @param taskName
     * @return
     */
    public boolean hasTask(String groupName, String taskName) {
        CronTask task = taskContainer.getTask(groupName, taskName);
        return task != null;
    }

    /**
     * 删除任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     */
    public void removeTask(String groupName, String taskName) throws Exception {
        taskContainer.removeTask(taskName, groupName);
    }

    /**
     * 获取指定分组的任务集合
     *
     * @param groupName
     * @return
     */
    public List<CronTask> getTasks(String groupName) {
        ScheduleTaskGroup taskGroup = taskContainer.getTaskGroup(groupName);
        List<CronTask> tasks = new ArrayList<CronTask>();
        if (taskGroup != null) {
            tasks.addAll(taskGroup.getTasks());
        }
        return tasks;
    }

    /**
     * 获取容器中的全部任务分组
     *
     * @return
     */
    public List<ScheduleTaskGroup> getTaskGroups() {
        List<ScheduleTaskGroup> taskGroups = new ArrayList<ScheduleTaskGroup>();
        taskGroups.addAll(taskContainer.getTaskGroup());
        return taskGroups;
    }

    /**
     * 获取指定任务
     *
     * @param groupName
     * @param taskName
     * @return
     */
    public CronTask getTask(String groupName, String taskName) {
        return taskContainer.getTask(groupName, taskName);
    }

    /**
     * 开始指定任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     */
    public void startTask(String groupName, String taskName) throws Exception {
        taskContainer.startTask(taskName, groupName);
    }

    /**
     * 暂停指定任务
     *
     * @param groupName 任务分组名称
     * @param taskName  任务名称
     * @throws Exception
     */
    public void pauseTask(String groupName, String taskName) throws Exception {
        taskContainer.pauseTask(taskName, groupName);
    }

    /**
     * 跟新任务，先删除，再添加
     *
     * @param groupName
     * @param taskName
     * @param task
     * @throws Exception
     */
    public void updateTask(String groupName, String taskName, CronTask task) throws Exception {
        removeTask(groupName, taskName);
        addTask(groupName, task);
    }

    /**
     * 添加任务分组的机器列表
     *
     * @param groupName 任务分组名称
     * @param machines  机器列表
     */
    public void addTaskGroupMachines(String groupName, List<String> machines) {
        ScheduleTaskGroup taskGroup = taskContainer.getTaskGroup(groupName);
        //如果为空，创建任务分组
        if (taskGroup == null) {
            taskGroup = taskContainer.getTaskGroup(groupName);
        }
        taskGroup.addMachines(machines);
    }

    /**
     * 添加机器
     *
     * @param groupName 任务分组名称
     * @param machine   机器
     */
    public void addTaskGroupMachine(String groupName, String machine) {
        ScheduleTaskGroup taskGroup = taskContainer.getTaskGroup(groupName);
        //如果为空，创建任务分组
        if (taskGroup == null) {
            taskGroup = taskContainer.getTaskGroup(groupName);
        }
        taskGroup.addMachines(machine);
    }

    /**
     * 删除任务分组的机器
     *
     * @param groupName 任务分组
     * @param machine   机器
     */
    public void removeTaskGroupMachine(String groupName, String machine) {
        ScheduleTaskGroup taskGroup = taskContainer.getTaskGroup(groupName);
        if (taskGroup != null) {
            taskGroup.removeMachine(machine);
        }
    }

    /**
     * 查询分组的机器列表
     *
     * @param groupName 分组名称
     * @return
     */
    public List<String> getTaskGroupMachines(String groupName) {
        List<String> machines = null;
        ScheduleTaskGroup taskGroup = taskContainer.getTaskGroup(groupName);
        if (taskGroup != null) {
            machines = taskGroup.getMachines();
        }
        return machines;
    }

    /**
     * 开启容器
     *
     * @throws Exception
     */
    public void startContainer() throws Exception {
        taskContainer.start();
    }

    /**
     * 停止容器
     *
     * @throws Exception
     */
    public void stopContainer() throws Exception {
        taskContainer.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("init and start task container...");
        try {
            //启动容器
            startContainer();
        } catch (Exception e) {
            log.error("start container error:{}", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        stopContainer();
    }
}
