package com.zjp.schedule.curator;

import com.zjp.schedule.discovery.ScheduleDiscovery;
import com.zjp.schedule.discovery.WatcherHandler;
import com.zjp.schedule.domain.ScheduleData;
import com.zjp.schedule.domain.ScheduleServerProperties;
import com.zjp.schedule.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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
 * Module Desc:com.zjp.schedule.curator
 * User: zjprevenge
 * Date: 2017/2/18
 * Time: 12:58
 */

public class CuratorClientManager {
    private static final Logger log = LoggerFactory.getLogger(CuratorClientManager.class);

    private static final String DATA_PATH = "data";
    private static final String MACHINE_PATH = "machine";

    private CuratorFramework client;

    private ScheduleServerProperties scheduleServerProperties;

    public CuratorClientManager() {
    }

    public CuratorClientManager(ScheduleServerProperties scheduleServerProperties) {
        this.scheduleServerProperties = scheduleServerProperties;
        init();
    }

    private void init() {
        client = CuratorFrameworkFactory.builder()
                .connectString(scheduleServerProperties.getUrl())
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(scheduleServerProperties.getNameSpace())
                .build();
        client.start();
    }

    public ScheduleServerProperties getScheduleServerProperties() {
        return scheduleServerProperties;
    }

    public void setScheduleServerProperties(ScheduleServerProperties scheduleServerProperties) {
        this.scheduleServerProperties = scheduleServerProperties;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    /**
     * 判断路径的加点是否存在
     *
     * @param path
     * @return
     * @throws Exception
     */
    public boolean checkPath(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    /**
     * 获取节点数据
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String getData(String path) throws Exception {
        byte[] bytes = client.getData().forPath(path);
        return new String(bytes, "utf-8");
    }

    /**
     * 添加数据
     * 不存在时，创建节点
     * 存在时，更新数据
     *
     * @param path
     * @param data
     * @param createMode
     * @throws Exception
     */
    public void addData(String path, String data, CreateMode createMode) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(createMode)
                    .forPath(path, data.getBytes());
        } else {
            client.setData().forPath(path, data.getBytes());
        }
    }

    /**
     * 获取节点的子节点路径
     * namespace 为每一个添加的节点自动加上namespace 作为node path的root
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getChildPath(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            return null;
        }
        return client.getChildren().forPath(path);
    }

    /**
     * 获取定时任务配置信息
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    public ScheduleData getScheduleData(String parentPath, String path) throws Exception {
        if (StringUtils.isEmpty(parentPath) || !parentPath.contains(DATA_PATH)) {
            throw new RuntimeException("path is error");
        }
        String data = getData(parentPath + ScheduleDiscovery.SEPARATOR + path);
        return JsonUtils.json2Bean(data, ScheduleData.class);
    }

    /**
     * 全路径获取数据
     *
     * @param path
     * @return
     * @throws Exception
     */
    public ScheduleData getScheduleData(String path) throws Exception {
        if (StringUtils.isEmpty(path) || !path.contains(DATA_PATH)) {
            throw new RuntimeException("path is error");
        }
        String data = getData(path);
        return JsonUtils.json2Bean(data, ScheduleData.class);
    }

    /**
     * 获取应用机器节点数据
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    public List<String> getMachineData(String path) throws Exception {
        if (StringUtils.isEmpty(path) || !path.contains(MACHINE_PATH)) {
            throw new RuntimeException("path is error");
        }
        List<String> machines = new ArrayList<String>();
        for (String childPath : getChildPath(path)) {
            log.info("childPath:{}", childPath);
            machines.add(getData(path + ScheduleDiscovery.SEPARATOR + childPath));
        }
        return machines;
    }

    /**
     * 给节点添加监听器
     *
     * @param nodeCache
     * @param handler
     */
    public void addWatcher(PathChildrenCache nodeCache, final WatcherHandler handler) throws Exception {
        nodeCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                log.info("type:{}", event.getType());
                switch (event.getType()) {
                    case CHILD_ADDED:
                        handler.handle(new String(event.getData().getData(), "utf-8"), NodeAction.NODE_ADDED);
                        break;
                    case CHILD_UPDATED:
                        handler.handle(new String(event.getData().getData(), "utf-8"), NodeAction.NODE_UPDATED);
                        break;
                    case CHILD_REMOVED:
                        handler.handle(new String(event.getData().getData(), "utf-8"), NodeAction.NODE_REMOVED);
                        break;
                    default:
                        break;
                }
            }
        }, Executors.newSingleThreadExecutor());
        nodeCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    /**
     * 删除节点
     *
     * @param path 节点路径
     * @throws Exception
     */
    public void deleteData(String path) throws Exception {
        client.delete().forPath(path);
    }

}
