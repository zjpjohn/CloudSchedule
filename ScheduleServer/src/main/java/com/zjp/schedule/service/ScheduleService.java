package com.zjp.schedule.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.zjp.schedule.core.task.CronTask;
import com.zjp.schedule.core.task.ScheduleTaskGroup;
import com.zjp.schedule.curator.CuratorClientManager;
import com.zjp.schedule.discovery.ScheduleDiscovery;
import com.zjp.schedule.domain.ScheduleData;
import com.zjp.schedule.domain.ScheduleServerProperties;
import com.zjp.schedule.domain.TaskGroupVO;
import com.zjp.schedule.domain.TaskVO;
import com.zjp.schedule.schedule.ScheduleServerContainer;
import com.zjp.schedule.util.JsonUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
 * Module Desc:com.zjp.schedule.service
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 11:58
 */
@Service
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    //更新和删除任务使用
    @Resource
    private CuratorClientManager curatorClientManager;
    //任务调度使用
    @Resource
    private ScheduleServerContainer scheduleServerContainer;

    @Resource
    private ScheduleServerProperties scheduleServerProperties;

    /**
     * 获取调度器中的任务分组
     *
     * @return 返回分组集合
     */
    public List<TaskGroupVO> getTaskGroups() {
        List<ScheduleTaskGroup> taskGroups = scheduleServerContainer.getTaskGroups();
        List<TaskGroupVO> groupVOs = Lists.transform(taskGroups, new Function<ScheduleTaskGroup, TaskGroupVO>() {
            @Override
            public TaskGroupVO apply(ScheduleTaskGroup taskGroup) {
                TaskGroupVO groupVO = new TaskGroupVO();
                String taskGroupName = taskGroup.getGroupName();
                groupVO.setGroupName(taskGroupName);
                groupVO.setApp(taskGroupName.split(":")[0]);
                groupVO.setMachines(taskGroup.getMachines());
                return groupVO;
            }
        });
        return groupVOs.stream()
                .filter(groupVO -> !groupVO.getGroupName().equals("default_group"))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定分组下的任务集合
     *
     * @param groupName 分组名称
     * @return
     */
    public List<TaskVO> getTasks(final String groupName) {
        List<CronTask> tasks = scheduleServerContainer.getTasks(groupName);
        List<TaskVO> taskVOs = Lists.transform(tasks, new Function<CronTask, TaskVO>() {
            @Override
            public TaskVO apply(CronTask task) {
                TaskVO taskVO = new TaskVO();
                taskVO.setGroupName(groupName);
                taskVO.setApp(groupName.split(":")[0]);
                taskVO.setCron(task.getExpression());
                taskVO.setTaskName(task.getKey());
                taskVO.setJobName(task.getJob().getName());
                return taskVO;
            }
        });
        return taskVOs;
    }

    public TaskVO getTask(String groupName, String taskName) {
        CronTask task = scheduleServerContainer.getTask(groupName, taskName);
        if (task != null) {
            TaskVO taskVO = new TaskVO();
            taskVO.setGroupName(groupName);
            taskVO.setTaskName(taskName);
            taskVO.setApp(groupName.split(":")[0]);
            taskVO.setJobName(task.getJob().getName());
            taskVO.setCron(task.getExpression());
            return taskVO;
        }
        return null;
    }

    /**
     * 启动指定的任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     */
    public void startTask(String groupName, String taskName) throws Exception {
        scheduleServerContainer.startTask(groupName, taskName);
    }

    /**
     * 暂停指定的任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     */
    public void pauseTask(String groupName, String taskName) throws Exception {
        scheduleServerContainer.pauseTask(groupName, taskName);
    }

    /**
     * 删除指定任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     */
    public void deleteTask(String groupName, String taskName) throws Exception {
        String path = new StringBuilder(ScheduleDiscovery.TASK)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(groupName)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(ScheduleDiscovery.DATA)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(taskName).toString();
        //并暂停任务
        scheduleServerContainer.pauseTask(groupName, taskName);
        //删除节点
        curatorClientManager.deleteData(path);
    }

    /**
     * 更新指定任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @param cron      任务表达式
     */
    public void updateTask(String groupName, String taskName, String cron) throws Exception {
        String path = new StringBuilder(ScheduleDiscovery.TASK)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(groupName)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(ScheduleDiscovery.DATA)
                .append(ScheduleDiscovery.SEPARATOR)
                .append(taskName).toString();
        ScheduleData scheduleData = curatorClientManager.getScheduleData(path);
        scheduleData.setCron(cron);
        curatorClientManager.addData(path, JsonUtils.bean2Json(scheduleData), CreateMode.PERSISTENT);
    }

}
