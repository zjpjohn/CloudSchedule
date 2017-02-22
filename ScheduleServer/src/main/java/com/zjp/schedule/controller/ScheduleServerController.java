package com.zjp.schedule.controller;

import com.zjp.schedule.domain.JsonResult;
import com.zjp.schedule.domain.TaskGroupVO;
import com.zjp.schedule.domain.TaskVO;
import com.zjp.schedule.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
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
 * Module Desc:com.zjp.schedule.controller
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 12:00
 */
@Controller
@RequestMapping("/server")
public class ScheduleServerController {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServerController.class);

    @Resource
    private ScheduleService scheduleService;

    /**
     * 任务分组页面
     *
     * @return
     */
    @RequestMapping("/schedule")
    public ModelAndView schedule() {
        List<TaskGroupVO> groupVOs = scheduleService.getTaskGroups();
        ModelAndView view = new ModelAndView("schedule");
        view.addObject("groupVOs", groupVOs);
        return view;
    }

    /**
     * 查看指定分组的定时任务信息
     *
     * @param groupName 任务分组
     * @return
     */
    @RequestMapping("/tasks/{groupName}")
    public ModelAndView tasks(@PathVariable String groupName) {
        List<TaskVO> taskVOs = scheduleService.getTasks(groupName);
        ModelAndView view = new ModelAndView("tasks");
        view.addObject("taskVOs", taskVOs);
        return view;
    }

    /**
     * 查询指定任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @return
     */
    @RequestMapping("/task/{groupName}/{taskName}")
    @ResponseBody
    public JsonResult<TaskVO> task(@PathVariable("groupName") String groupName,
                                   @PathVariable("taskName") String taskName) {
        TaskVO taskVO = scheduleService.getTask(groupName, taskName);
        return JsonResult.success("查询任务信息").setData(taskVO);
    }

    /**
     * 启动任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @return
     */
    @RequestMapping("/start/{groupName}/{taskName}")
    @ResponseBody
    public JsonResult startTask(@PathVariable String groupName,
                                @PathVariable String taskName) {
        try {
            scheduleService.startTask(groupName, taskName);
            return JsonResult.success("启动任务成功");
        } catch (Exception e) {
            log.error("启动任务失败：{}", e);
        }
        return JsonResult.error("启动任务失败");
    }

    /**
     * 暂停任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @return
     */
    @RequestMapping("/pause/{groupName}/{taskName}")
    @ResponseBody
    public JsonResult pauseTask(@PathVariable String groupName,
                                @PathVariable String taskName) {
        try {
            scheduleService.pauseTask(groupName, taskName);
            return JsonResult.success("暂停任务成功");
        } catch (Exception e) {
            log.error("暂停任务失败:{}", e);
        }
        return JsonResult.error("暂停任务失败");
    }

    /**
     * 删除任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @return
     */
    @RequestMapping("/delete/{groupName}/{taskName}")
    @ResponseBody
    public JsonResult deleteTask(@PathVariable String groupName,
                                 @PathVariable String taskName) {
        try {
            scheduleService.deleteTask(groupName, taskName);
            return JsonResult.success("删除任务成功");
        } catch (Exception e) {
            log.error("删除任务失败：{}", e);
        }
        return JsonResult.error("删除任务失败");
    }

    /**
     * 更新任务
     *
     * @param groupName 任务分组
     * @param taskName  任务名称
     * @param cron      时间表达式
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updateTask(String groupName,
                                 String taskName,
                                 String cron) {
        try {
            scheduleService.updateTask(groupName, taskName, cron);
            return JsonResult.success("更新任务成功");
        } catch (Exception e) {
            log.error("更新任务失败：{}", e);
        }
        return JsonResult.error("更新任务失败");
    }
}
