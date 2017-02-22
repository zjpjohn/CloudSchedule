package com.zjp.schedule.core.task;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.util.concurrent.*;

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
 * Date: 2017/2/21
 * Time: 20:33
 */

public class ThreadPoolTaskSchedule {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolTaskSchedule.class);

    private volatile ScheduledExecutorService scheduledExecutor;

    private static final boolean setRemoveOnCancelPolicyAvailable =
            ClassUtils.hasMethod(ScheduledThreadPoolExecutor.class, "setRemoveOnCancelPolicy", boolean.class);

    private volatile int poolSize = 16;

    private volatile boolean removeOnCancelPolicy = false;

    public ThreadPoolTaskSchedule(int poolSize, final String scheduleName) {
        this.poolSize = poolSize;
        scheduledExecutor = createExecutor(poolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, scheduleName);
            }
        }, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void setPoolSize(int poolSize) {
        Preconditions.checkArgument(poolSize > 0, "poolSize must bigger than 1");
        this.poolSize = poolSize;
        if (scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setCorePoolSize(poolSize);
        }
    }

    private void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
        if (setRemoveOnCancelPolicyAvailable && this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setRemoveOnCancelPolicy(removeOnCancelPolicy);
        } else if (removeOnCancelPolicy && this.scheduledExecutor != null) {
            log.info("Could not apply remove-on-cancel policy - not a Java 7+ ScheduledThreadPoolExecutor");
        }
    }

    protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
                                                 RejectedExecutionHandler rejectedExecutionHandler) {
        this.scheduledExecutor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (this.removeOnCancelPolicy) {
            if (setRemoveOnCancelPolicyAvailable && this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
                ((ScheduledThreadPoolExecutor) this.scheduledExecutor).setRemoveOnCancelPolicy(true);
            }
        }
        return this.scheduledExecutor;
    }

    protected ScheduledThreadPoolExecutor createExecutor(int poolSize,
                                                         ThreadFactory threadFactory,
                                                         RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }

    public ScheduledExecutorService getScheduledExecutor() {
        Preconditions.checkArgument(this.scheduledExecutor != null, "scheduledExecutor must not be null");
        return this.scheduledExecutor;
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        Preconditions.checkArgument(this.scheduledExecutor instanceof ScheduledThreadPoolExecutor,
                "No ScheduledThreadPoolExecutor available");
        return (ScheduledThreadPoolExecutor) scheduledExecutor;
    }

    public int getPoolSize() {
        if (scheduledExecutor == null) {
            return this.poolSize;
        }
        return getScheduledThreadPoolExecutor().getPoolSize();
    }

    public boolean isRemoveOnCancelPolicy() {
        if (!setRemoveOnCancelPolicyAvailable) {
            return false;
        }
        if (scheduledExecutor == null) {
            return this.removeOnCancelPolicy;
        }
        return getScheduledThreadPoolExecutor().getRemoveOnCancelPolicy();
    }

    public int getActiveCount() {
        if (scheduledExecutor == null) {
            return 0;
        }
        return getScheduledThreadPoolExecutor().getActiveCount();
    }


    public ScheduledFuture<?> schedule(CronTask cronTask) {
        ScheduledExecutorService executor = getScheduledExecutor();
        ScheduledFuture<?> scheduledFuture = new ReschedulingRunnable(cronTask.getTrigger(), executor, cronTask).schedule();
        return scheduledFuture;
    }
}
