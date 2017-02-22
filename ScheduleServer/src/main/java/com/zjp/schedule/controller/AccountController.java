package com.zjp.schedule.controller;

import com.zjp.schedule.domain.JsonResult;
import com.zjp.schedule.domain.SystemManager;
import com.zjp.schedule.service.SystemManagerService;
import com.zjp.schedule.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
 * Time: 12:05
 */
@Controller
@RequestMapping("/user")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Resource
    private SystemManagerService systemManagerService;

    /**
     * 用户登录页面跳转
     *
     * @return
     */
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 注册页面跳转
     *
     * @return
     */
    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 用户登录
     *
     * @param name
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult verify(String name,
                             String password,
                             HttpServletRequest request) {
        if (StringUtils.isEmpty(name)
                || StringUtils.isEmpty(password)) {
            return JsonResult.error("用户名或密码不允许为空");
        }
        if (systemManagerService.login(name, password)) {
            log.info("login user ...");
            HttpSession session = request.getSession();
            session.setAttribute("name", name);
            return JsonResult.success("登录成功").setData("/index");
        }
        return JsonResult.error("登录失败");
    }

    /**
     * 注册用户
     *
     * @param name
     * @param password
     * @param confirm
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/registry", method = RequestMethod.POST)
    public JsonResult registry(String name,
                               String password,
                               String confirm,
                               HttpServletRequest request) {
        if (StringUtils.isEmpty(name)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(confirm)) {
            return JsonResult.error("参数不允许为空");
        }
        log.info("registry user ....");
        if (!confirm.equals(password)) {
            return JsonResult.error("两次输入密码不同");
        }
        password = MD5Utils.md5(password);
        SystemManager manager = new SystemManager();
        manager.setName(name);
        manager.setPassword(password);
        JsonResult<String> result = systemManagerService.register(manager, "/index");
        if (result.getStatus() == 200) {
            request.getSession().setAttribute("name", name);
        }
        return result;
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/user/login";
    }
}
