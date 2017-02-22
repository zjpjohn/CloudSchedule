package com.zjp.schedule.schedule;

import com.zjp.schedule.core.job.Job;
import com.zjp.schedule.core.job.JobException;
import com.zjp.schedule.netty.NettyClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

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
 * Time: 19:33
 */

public class ScheduleInvokeJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(ScheduleInvokeJob.class);

    private List<String> machines;

    private String key;

    private NettyClientManager nettyClientManager;

    public ScheduleInvokeJob() {
    }

    public ScheduleInvokeJob(List<String> machines,
                             String key,
                             NettyClientManager nettyClientManager) {
        this.machines = machines;
        this.key = key;
        this.nettyClientManager = nettyClientManager;
    }

    public List<String> getMachines() {
        return machines;
    }

    public ScheduleInvokeJob setMachines(List<String> machines) {
        this.machines = machines;
        return this;
    }

    public String getKey() {
        return key;
    }

    public ScheduleInvokeJob setKey(String key) {
        this.key = key;
        return this;
    }

    public NettyClientManager getNettyClientManager() {
        return nettyClientManager;
    }

    public ScheduleInvokeJob setNettyClientManager(NettyClientManager nettyClientManager) {
        this.nettyClientManager = nettyClientManager;
        return this;
    }

    /**
     * 获得作业名称
     *
     * @return 作业名称
     */
    @Override
    public String getName() {
        return key;
    }

    /**
     * 作业执行内容
     *
     * @return 作业是否执行成功 true: 作业执行成功; false: 作业执行失败
     * @throws JobException 作业执行异常
     */
    @Override
    public boolean execute() throws JobException {
        if (machines == null || machines.size() == 0) {
            throw new RuntimeException("没有可用的机器列表");
        }
        //随机调用任务
        Random random = new Random();
        int selectMachineIndex = random.nextInt(machines.size());
        String machine = machines.get(selectMachineIndex);
        String[] split = machine.split(":");
        //执行远程调用任务
        String response = nettyClientManager.scheduleInvoke(split[0], Integer.valueOf(split[1]), key);
        log.info("任务执行完成，返回结果:{}", response);
        return true;
    }
}
