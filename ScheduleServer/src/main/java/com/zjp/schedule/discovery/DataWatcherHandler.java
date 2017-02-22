package com.zjp.schedule.discovery;

import com.zjp.schedule.core.task.CronTask;
import com.zjp.schedule.curator.NodeAction;
import com.zjp.schedule.domain.ScheduleData;
import com.zjp.schedule.netty.NettyClientManager;
import com.zjp.schedule.schedule.ScheduleInvokeJob;
import com.zjp.schedule.schedule.ScheduleServerContainer;
import com.zjp.schedule.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Module Desc:com.zjp.schedule.discovery
 * User: zjprevenge
 * Date: 2017/2/18
 * Time: 16:40
 */

public class DataWatcherHandler implements WatcherHandler {

    private static final Logger log = LoggerFactory.getLogger(DataWatcherHandler.class);

    private String group;

    private ScheduleServerContainer scheduleServerContainer;

    private NettyClientManager nettyClientManager;

    public DataWatcherHandler() {
    }

    public DataWatcherHandler(String group,
                              ScheduleServerContainer scheduleServerContainer,
                              NettyClientManager nettyClientManager) {
        this.group = group;
        this.scheduleServerContainer = scheduleServerContainer;
        this.nettyClientManager = nettyClientManager;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ScheduleServerContainer getScheduleServerContainer() {
        return scheduleServerContainer;
    }

    public void setScheduleServerContainer(ScheduleServerContainer scheduleServerContainer) {
        this.scheduleServerContainer = scheduleServerContainer;
    }

    public NettyClientManager getNettyClientManager() {
        return nettyClientManager;
    }

    public void setNettyClientManager(NettyClientManager nettyClientManager) {
        this.nettyClientManager = nettyClientManager;
    }

    /**
     * 节点监听处理
     *
     * @param data       节点数据
     * @param nodeAction 节点变化类型
     */
    @Override
    public void handle(String data, NodeAction nodeAction)throws Exception {
        try {
            ScheduleData scheduleData = JsonUtils.json2Bean(data, ScheduleData.class);
            switch (nodeAction) {
                case NODE_ADDED:
                    ScheduleInvokeJob job = new ScheduleInvokeJob();
                    if (!scheduleServerContainer.hasTask(group, scheduleData.getKey())) {
                        job.setKey(scheduleData.getKey())
                                .setNettyClientManager(nettyClientManager)
                                .setMachines(scheduleServerContainer.getTaskGroupMachines(group));
                        CronTask task = new CronTask(scheduleData.getKey(), scheduleData.getCron(), job);
                        scheduleServerContainer.addTask(group, task);
                    }
                    break;
                case NODE_REMOVED:
                    scheduleServerContainer.removeTask(group, scheduleData.getKey());
                    break;
                case NODE_UPDATED:
                    ScheduleInvokeJob jobNew = new ScheduleInvokeJob();
                    jobNew.setKey(scheduleData.getKey())
                            .setNettyClientManager(nettyClientManager)
                            .setMachines(scheduleServerContainer.getTaskGroupMachines(group));
                    CronTask taskNew = new CronTask(scheduleData.getKey(), scheduleData.getCron(), jobNew);
                    scheduleServerContainer.updateTask(group, scheduleData.getKey(), taskNew);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("watch data error:{}", e);
        }
    }
}
