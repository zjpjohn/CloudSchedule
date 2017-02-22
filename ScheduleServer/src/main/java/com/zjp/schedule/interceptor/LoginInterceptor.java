package com.zjp.schedule.interceptor;

import com.zjp.schedule.exception.UnAuthorizedException;
import com.zjp.schedule.util.AjaxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * Module Desc:com.zjp.schedule.interceptor
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 12:01
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    private String[] excludeUrls = {"css", "js", "img", "fonts",
            "scripts", "login", "verify", "register", "/error",
            "registry", "/info", "/health",
            "/metrics", "/trace", "/mappings",
            "/env", "/beans", "/dump", "/autoconfig",
            "/configprops"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        for (String excludeUrl : excludeUrls) {
            if (requestURI.contains(excludeUrl)) {
                return true;
            }
        }
        //session过期判断
        HttpSession session = request.getSession();
        String name = (String) session.getAttribute("name");
        if (name == null) {
            if (AjaxUtils.isAjax(request)) {
                response.setHeader("sessionStatus", "timeout");
                return false;
            } else {
                throw new UnAuthorizedException("需要登录验证");
            }
        }
        return true;
    }
}
