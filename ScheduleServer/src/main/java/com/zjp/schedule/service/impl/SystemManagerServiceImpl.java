package com.zjp.schedule.service.impl;

import com.zjp.schedule.domain.JsonResult;
import com.zjp.schedule.domain.SystemManager;
import com.zjp.schedule.mapper.SystemManagerMapper;
import com.zjp.schedule.service.SystemManagerService;
import com.zjp.schedule.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Project: AlertSystem
 * Module Desc:com.juntu.alert.service.impl
 * User: zjprevenge
 * Date: 2016/12/7
 * Time: 16:21
 */
@Service
public class SystemManagerServiceImpl implements SystemManagerService {

    private static final Logger log = LoggerFactory.getLogger(SystemManagerServiceImpl.class);

    @Resource
    private SystemManagerMapper systemManagerMapper;

    /**
     * 登录验证
     *
     * @param name     用户名
     * @param password 密码
     * @return
     */
    @Override
    public boolean login(String name, String password) {
        SystemManager manager = systemManagerMapper.getSystemManagerByName(name);
        if (manager != null
                && MD5Utils.md5(password)
                .equals(manager.getPassword())) {
            return true;
        }
        return false;
    }

    /**
     * 注册用户
     *
     * @param redirect      注册成功跳转地址
     * @param systemManager 注册用户信息
     * @return
     */
    @Override
    public JsonResult<String> register(SystemManager systemManager, String redirect) {
        SystemManager manager = systemManagerMapper.getSystemManagerByName(systemManager.getName());
        if (manager != null) {
            return JsonResult.error("注册用户名已经存在");
        }
        try {
            systemManagerMapper.addSystemManager(systemManager);
            return JsonResult.success("注册用户成功").setData(redirect);
        } catch (Exception e) {
            log.error("注册用户失败：{}", e);
        }
        return JsonResult.error("注册用户失败");
    }

    @Override
    public boolean updateUser(SystemManager systemManager) {
        return false;
    }
}
