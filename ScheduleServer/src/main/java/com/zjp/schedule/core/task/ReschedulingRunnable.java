package com.zjp.schedule.core.task;

import com.zjp.schedule.core.job.JobException;
import com.zjp.schedule.core.trigger.SimpleTriggerContext;
import com.zjp.schedule.core.trigger.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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
 * Time: 20:38
 */

public class ReschedulingRunnable implements ScheduledFuture<Object>, Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReschedulingRunnable.class);

    private final Trigger trigger;

    private SimpleTriggerContext triggerContext = new SimpleTriggerContext();

    private ScheduledFuture<?> currentFuture;

    private final ScheduledExecutorService executor;

    private Date scheduledExecutionTime;

    private final Object triggerContextMonitor = new Object();

    private final CronTask cronTask;

    public ReschedulingRunnable(Trigger trigger,
                                ScheduledExecutorService executor,
                                CronTask cronTask) {
        this.trigger = trigger;
        this.executor = executor;
        this.cronTask = cronTask;
    }

    public ScheduledFuture<?> schedule() {
        synchronized (this.triggerContextMonitor) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            //首次调用，立即执行
            if (scheduledExecutionTime == null) {
                return null;
            }
            long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
            this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    @Override
    public void run() {
        Date actualExecutionTime = new Date();
        try {
            cronTask.getJob().execute();
            log.info(cronTask.getKey() + "<-->" + cronTask.getExpression() + "<-->" + Thread.currentThread().getName());
        } catch (JobException e) {
            log.error("job execute error:{}", e);
        }
        Date completionTime = new Date();
        synchronized (this.triggerContextMonitor) {
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!this.currentFuture.isCancelled()) {
                schedule();
            }
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.getDelay(unit);
    }


    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.cancel(mayInterruptIfRunning);
        }
    }


    @Override
    public boolean isCancelled() {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.isCancelled();
        }
    }


    @Override
    public boolean isDone() {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.isDone();
        }
    }


    @Override
    public Object get() throws InterruptedException, ExecutionException {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.get();
    }


    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.get(timeout, unit);
    }
}
