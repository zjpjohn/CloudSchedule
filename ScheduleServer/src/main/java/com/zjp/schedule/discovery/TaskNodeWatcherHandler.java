package com.zjp.schedule.discovery;

import com.zjp.schedule.curator.NodeAction;
import org.apache.commons.lang3.StringUtils;

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
 * Date: 2017/2/22
 * Time: 14:38
 */

public class TaskNodeWatcherHandler implements WatcherHandler {

    private ScheduleDiscovery scheduleDiscovery;

    public TaskNodeWatcherHandler() {
    }

    public TaskNodeWatcherHandler(ScheduleDiscovery scheduleDiscovery) {
        this.scheduleDiscovery = scheduleDiscovery;
    }

    public ScheduleDiscovery getScheduleDiscovery() {
        return scheduleDiscovery;
    }

    public void setScheduleDiscovery(ScheduleDiscovery scheduleDiscovery) {
        this.scheduleDiscovery = scheduleDiscovery;
    }

    /**
     * 节点监听处理
     *
     * @param data       节点数据
     * @param nodeAction 节点变化类型
     */
    @Override
    public void handle(String data, NodeAction nodeAction) throws Exception {
        //延时，防止数据还未上传至zookeeper
        Thread.sleep(1000);
        if (StringUtils.isNotEmpty(data)) {
            String path = new StringBuilder(ScheduleDiscovery.TASK)
                    .append(ScheduleDiscovery.SEPARATOR)
                    .append(data).toString();
            switch (nodeAction) {
                case NODE_ADDED://当有项目增加时，发现项目，并添加监控
                    scheduleDiscovery.discovery(path);
                    scheduleDiscovery.watchNodes(path);
                    break;
                case NODE_REMOVED:
                    break;
                case NODE_UPDATED:
                    break;
                default:
                    break;
            }
        }
    }
}
