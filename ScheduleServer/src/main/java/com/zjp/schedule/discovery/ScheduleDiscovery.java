package com.zjp.schedule.discovery;

import com.zjp.schedule.core.task.CronTask;
import com.zjp.schedule.curator.CuratorClientManager;
import com.zjp.schedule.domain.ScheduleData;
import com.zjp.schedule.netty.NettyClientManager;
import com.zjp.schedule.schedule.ScheduleInvokeJob;
import com.zjp.schedule.schedule.ScheduleServerContainer;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Module Desc:com.zjp.schedule.discovery
 * User: zjprevenge
 * Date: 2017/2/18
 * Time: 15:02
 */

public class ScheduleDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ScheduleDiscovery.class);

    //路径分隔符
    public static final String SEPARATOR = "/";
    public static final String DATA = "data";
    public static final String MACHINE = "machine";
    public static final String TASK = "/task";

    //zookeeper客户端
    private CuratorClientManager curatorClientManager;
    //调度容器
    private ScheduleServerContainer scheduleServerContainer;
    //远程调用器
    private NettyClientManager nettyClientManager;

    public ScheduleDiscovery() {
    }

    public ScheduleDiscovery(CuratorClientManager curatorClientManager,
                             ScheduleServerContainer scheduleServerContainer,
                             NettyClientManager nettyClientManager) {
        this.curatorClientManager = curatorClientManager;
        this.scheduleServerContainer = scheduleServerContainer;
        this.nettyClientManager = nettyClientManager;
    }

    public CuratorClientManager getCuratorClientManager() {
        return curatorClientManager;
    }

    public void setCuratorClientManager(CuratorClientManager curatorClientManager) {
        this.curatorClientManager = curatorClientManager;
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
     * 拉取zookeeper全部数据信息
     * 首次加载数据
     */
    public void discovery(String path) throws Exception {

        String groupName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
        //添加任务分组
        scheduleServerContainer.addTaskGroup(groupName);
        String dataPath = new StringBuilder(path).append(SEPARATOR).append(DATA).toString();
        String machinePath = new StringBuilder(path).append(SEPARATOR).append(MACHINE).toString();
        log.info("machinePath:{}", machinePath);
        //添加任务分组可用的机器
        List<String> machines = curatorClientManager.getMachineData(machinePath);
        log.info("machines:{}", machines);
        scheduleServerContainer.addTaskGroupMachines(groupName, machines);
        //获取调度data信息
        for (String schedulePath : curatorClientManager.getChildPath(dataPath)) {
            ScheduleData data = curatorClientManager.getScheduleData(dataPath, schedulePath);
            ScheduleInvokeJob job = new ScheduleInvokeJob();
            job.setKey(data.getKey())
                    .setNettyClientManager(nettyClientManager)
                    .setMachines(machines);
            CronTask task = new CronTask(data.getKey(), data.getCron(), job);
            scheduleServerContainer.addTask(groupName, task);
        }

    }

    /**
     * 监听节点数据变化
     * 节点变化，监听节点数据
     */
    public void watchNodes(String path) throws Exception {
        log.info("watch node path:{}", path);
        String groupName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
        String dataPath = new StringBuffer(path)
                .append(SEPARATOR)
                .append(DATA)
                .toString();
        String machinePath = new StringBuffer(path)
                .append(SEPARATOR)
                .append(MACHINE)
                .toString();
        curatorClientManager.addWatcher(new PathChildrenCache(curatorClientManager.getClient(), dataPath, true),
                new DataWatcherHandler(groupName, scheduleServerContainer, nettyClientManager));
        curatorClientManager.addWatcher(new PathChildrenCache(curatorClientManager.getClient(), machinePath, true),
                new MachineWatcherHandler(groupName, scheduleServerContainer, nettyClientManager));
    }


    /**
     * 监听/task节点的子节点
     * 针对调度器启动时没有项目的问题
     * PathChildrenCache 监听节点下一级的变化：添加、数据更新，删除等
     * /task节点 监听/task/node1 、/task/node2
     */
    public void watchTaskNode() throws Exception {
        //如果节点不存在，先创建
        if (!curatorClientManager.checkPath(TASK)) {
            curatorClientManager.getClient().create().withMode(CreateMode.PERSISTENT).forPath(TASK);
        }
        //为节点添加监控
        curatorClientManager.addWatcher(new PathChildrenCache(curatorClientManager.getClient(), TASK, true),
                new TaskNodeWatcherHandler(this));
    }
}
