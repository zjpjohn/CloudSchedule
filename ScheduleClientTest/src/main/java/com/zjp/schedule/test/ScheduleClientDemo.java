package com.zjp.schedule.test;

import com.zjp.schedule.annotation.QSchedule;
import com.zjp.schedule.annotation.Schedule;

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
 * Module Desc:com.zjp.schedule.test
 * User: zjprevenge
 * Date: 2017/2/20
 * Time: 15:16
 */
@QSchedule()
public class ScheduleClientDemo {

    @Schedule(cron = "*/10 * * * * ?")
    public void schedule1() {
        System.out.println("测试用例1");
    }

    @Schedule(cron = "*/10 * * * * ?", value = "test2")
    public void schedule2() {
        System.out.println("测试用例2");
    }
}
