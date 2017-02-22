package com.zjp.schedule.core.task;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
 * Date: 2017/2/22
 * Time: 0:04
 */

public class ScheduleTaskContainer {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskContainer.class);

    /*
     *默认容器名称
     */
    private static final String DEFAULT_NAME = "default-container";

    /**
     * 容器名称
     */
    private String name;
    /**
     * 任务分组列表
     */
    private Map<String, ScheduleTaskGroup> taskGroups = Maps.newConcurrentMap();

    /**
     * 最大任务分组数，默认10
     */
    private int maxGroup = 10;

    /**
     * 任务分组计数器
     */
    private AtomicInteger groupCount = new AtomicInteger(0);


    public ScheduleTaskContainer() {
        this(DEFAULT_NAME);
    }

    public ScheduleTaskContainer(String name) {
        this.name = name;
        taskGroups.put(ScheduleTaskGroup.DEFAULT, new ScheduleTaskGroup(ScheduleTaskGroup.DEFAULT));
    }

    /**
     * 添加指定名称的任务分组到容器
     *
     * @param groupName 分组名称
     * @return
     */
    public ScheduleTaskGroup addGroup(String groupName) {
        return this.addGroup(groupName, 20);
    }

    /**
     * 添加任务分组到容器中
     *
     * @param groupName
     * @param maxTask
     * @return
     */
    public ScheduleTaskGroup addGroup(String groupName, int maxTask) {
        ScheduleTaskGroup taskGroup = null;
        if (!taskGroups.containsKey(groupName)) {
            if (this.groupCount.get() < this.maxGroup) {
                taskGroup = new ScheduleTaskGroup(groupName);
                taskGroups.put(groupName, taskGroup);
                groupCount.incrementAndGet();
            } else {
                log.error("container={}; exception max group count is {}", name, maxGroup);
            }
        }
        return taskGroup;
    }

    /**
     * 获取指定名称的任务分组
     *
     * @param groupName 分组名称
     * @return
     */
    public ScheduleTaskGroup getTaskGroup(String groupName) {
        return taskGroups.get(groupName);
    }

    /**
     * 获取全部分组
     *
     * @return
     */
    public Collection<ScheduleTaskGroup> getTaskGroup() {
        return taskGroups.values();
    }

    /**
     * 添加任务到指定分组
     *
     * @param cronTask  任务
     * @param groupName 分组名称
     */
    public void addTask(CronTask cronTask, String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group == null) {
            group = addGroup(groupName);
        }
        if (group != null && cronTask != null) {
            group.addTask(cronTask);
        }
    }

    /**
     * 添加任务到容器中
     *
     * @param cronTask
     * @throws Exception
     */
    public void addTask(CronTask cronTask) throws Exception {
        String groupName = StringUtils.isNotEmpty(cronTask.getGroupName()) ? cronTask.getGroupName() : ScheduleTaskGroup.DEFAULT;
        this.addTask(cronTask, groupName);
    }

    /**
     * 删除指定任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @return
     * @throws Exception
     */
    public CronTask removeTask(String groupName, String taskName) throws Exception {
        ScheduleTaskGroup taskGroup = this.taskGroups.get(groupName);
        if (taskGroup != null) {
            CronTask cronTask = taskGroup.remove(taskName);
            return cronTask;
        }
        return null;
    }

    /**
     * 删除默认分组中的任务
     *
     * @param taskName 任务名称
     * @return
     * @throws Exception
     */
    public CronTask removeTask(String taskName) throws Exception {
        return this.removeTask(ScheduleTaskGroup.DEFAULT, taskName);
    }

    /**
     * 获取分组下指定的任务
     *
     * @param groupName 分组名称
     * @param taskName  任务名称
     * @return
     */
    public CronTask getTask(String groupName, String taskName) {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group == null) {
            return null;
        }
        return group.getTask(taskName);
    }


    /**
     * 停止任务分组中的所有任务
     *
     * @param groupName 分组名称
     */
    public void stopGroup(String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.pauseAll();
        }
    }

    /**
     * 移除任务容器中的分组
     *
     * @param groupName 分组名称
     * @throws Exception
     */
    public void removeGroup(String groupName) throws Exception {
        if (StringUtils.isNotEmpty(groupName)
                && ScheduleTaskGroup.DEFAULT.equals(groupName)) {
            stopGroup(groupName);
            ScheduleTaskGroup group = taskGroups.remove(groupName);
            if (group != null) {
                groupCount.decrementAndGet();
            }
        }
    }

    /**
     * 启动任务
     *
     * @param taskName
     * @param groupName
     * @throws Exception
     */
    public void startTask(String taskName, String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.startTask(taskName);
        }
    }

    /**
     * 启动指定分组中的全部任务
     *
     * @param groupName 分组名称
     * @throws Exception
     */
    public void startGroup(String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.startAll();
        }
    }

    /**
     * 启动容器中的全部任务
     *
     * @throws Exception
     */
    public void start() throws Exception {
        Set<String> keySet = taskGroups.keySet();
        for (String groupName : keySet) {
            if (StringUtils.isNotEmpty(groupName)) {
                startGroup(groupName);
            }
        }
    }

    /**
     * 暂停指定容器
     *
     * @throws Exception
     */
    public void pause() throws Exception {
        Set<String> keySet = taskGroups.keySet();
        for (String groupName : keySet) {
            pauseGroup(groupName);
        }
    }

    /**
     * 暂停指定分组的任务
     *
     * @param groupName
     * @throws Exception
     */
    public void pauseGroup(String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.pauseAll();
        }
    }

    /**
     * 暂停指定任务
     *
     * @param taskName  任务名称
     * @param groupName 任务分组名称
     * @throws Exception
     */
    public void pauseTask(String taskName, String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.pause(taskName);
        }
    }

    /**
     * 停止指定任务
     *
     * @param taskName  任务名称
     * @param groupName 任务分组
     */
    public void stopTask(String taskName, String groupName) throws Exception {
        ScheduleTaskGroup group = taskGroups.get(groupName);
        if (group != null) {
            group.pause(taskName);
        }
    }

    /**
     * 停止全部任务
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        Set<String> keySet = taskGroups.keySet();
        for (String groupName : keySet) {
            if (StringUtils.isNotEmpty(groupName)) {
                stopGroup(groupName);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ScheduleTaskGroup> getTaskGroups() {
        return taskGroups;
    }

    public void setTaskGroups(Map<String, ScheduleTaskGroup> taskGroups) {
        this.taskGroups = taskGroups;
    }

    public int getMaxGroup() {
        return maxGroup;
    }

    public void setMaxGroup(int maxGroup) {
        this.maxGroup = maxGroup;
    }

    public AtomicInteger getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(AtomicInteger groupCount) {
        this.groupCount = groupCount;
    }
}
