package com.zjp.schedule.core.trigger;

import java.util.Date;
import java.util.TimeZone;

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
 * Time: 20:52
 */

public class CronTrigger implements Trigger {

    private CronSequenceGenerator sequenceGenerator;

    public CronTrigger(String expression) {
        this.sequenceGenerator = new CronSequenceGenerator(expression);
    }

    public CronTrigger(String expression, TimeZone timeZone) {
        this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
    }


    public String getExpression() {
        return this.sequenceGenerator.getExpression();
    }


    /**
     * 获取下一次执行时间
     *
     * @param triggerContext
     * @return
     */
    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                date = scheduled;
            }
        } else {
            date = new Date();
        }
        return sequenceGenerator.next(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CronTrigger that = (CronTrigger) o;

        return sequenceGenerator != null ? sequenceGenerator.equals(that.sequenceGenerator) : that.sequenceGenerator == null;

    }

    @Override
    public int hashCode() {
        return sequenceGenerator != null ? sequenceGenerator.hashCode() : 0;
    }

    @Override
    public String toString() {
        return sequenceGenerator.toString();
    }
}
