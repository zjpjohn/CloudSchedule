package com.zjp.schedule.curator;

import com.zjp.schedule.bean.QScheduleProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

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
 * Date: 2017/2/17
 * Time: 19:55
 */

public class CuratorClientManager {

    public static final String DATA_PATH = "data";

    public static final String MACHINE_PATH = "machine";

    //阻塞，直到初始化完成
    public static final CountDownLatch LATCH = new CountDownLatch(1);

    private QScheduleProperties qScheduleProperties;

    private CuratorFramework client;

    public CuratorClientManager(QScheduleProperties qScheduleProperties) {
        this.qScheduleProperties = qScheduleProperties;
        init();
    }

    public QScheduleProperties getqScheduleProperties() {
        return qScheduleProperties;
    }

    public void setqScheduleProperties(QScheduleProperties qScheduleProperties) {
        this.qScheduleProperties = qScheduleProperties;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void init() {
        client = CuratorFrameworkFactory.builder()
                .connectString(qScheduleProperties.getUrl())
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(qScheduleProperties.getNameSpace())
                .build();
        client.start();
        LATCH.countDown();
    }

    /**
     * 判断路径是否存在
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    public Boolean checkPath(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    /**
     * 创建节点并存储数据
     *
     * @param path 节点路径
     * @param data 数据
     * @param mode 节点类型
     * @throws Exception
     */
    public void addData(String path, String data, CreateMode mode) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        //如果节点不存在，创建节点
        if (stat == null) {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(mode)
                    .forPath(path, data.getBytes());
        } else {
            //更新数据
            client.setData().forPath(path, data.getBytes());
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 节点路径
     * @return
     * @throws Exception
     */
    public String getData(String path) throws Exception {
        byte[] bytes = client.getData().forPath(path);
        return new String(bytes, "utf-8");
    }

    public void stop() {
        client.close();
    }
}
