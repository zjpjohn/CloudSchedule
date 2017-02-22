package com.zjp.schedule.discovery;

import com.zjp.schedule.curator.NodeAction;
import com.zjp.schedule.netty.NettyClientManager;
import com.zjp.schedule.schedule.ScheduleServerContainer;

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
 * Time: 16:41
 */

public class MachineWatcherHandler implements WatcherHandler {

    private String group;

    private ScheduleServerContainer scheduleServerContainer;

    private NettyClientManager nettyClientManager;

    public MachineWatcherHandler() {
    }

    public MachineWatcherHandler(String group,
                                 ScheduleServerContainer scheduleServerContainer,
                                 NettyClientManager nettyClientManager) {
        this.group = group;
        this.scheduleServerContainer = scheduleServerContainer;
        this.nettyClientManager = nettyClientManager;
    }

    public NettyClientManager getNettyClientManager() {
        return nettyClientManager;
    }

    public void setNettyClientManager(NettyClientManager nettyClientManager) {
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

    /**
     * 节点监听处理
     *
     * @param data       节点数据
     * @param nodeAction 节点变化类型
     */
    @Override
    public void handle(String data, NodeAction nodeAction) throws Exception {
        switch (nodeAction) {
            case NODE_ADDED:
                //添加到机器列表
                scheduleServerContainer.addTaskGroupMachine(group, data);
                break;
            case NODE_REMOVED:
                //从机器列表中删除
                scheduleServerContainer.removeTaskGroupMachine(group, data);
                //删除channel
                nettyClientManager.removeChannel(data);
                break;
            case NODE_UPDATED:
                break;
            default:
                break;
        }
    }
}
