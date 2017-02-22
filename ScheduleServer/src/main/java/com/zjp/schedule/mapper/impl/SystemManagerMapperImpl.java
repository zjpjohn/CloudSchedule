package com.zjp.schedule.mapper.impl;

import com.zjp.schedule.domain.SystemManager;
import com.zjp.schedule.mapper.SystemManagerMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

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
 * Module Desc:com.zjp.schedule.mapper.impl
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 22:12
 */
@Repository
public class SystemManagerMapperImpl implements SystemManagerMapper {

    @Resource
    private SqlSession sqlSession;

    @Override
    public int addSystemManager(SystemManager systemManager) {
        return sqlSession.insert("com.zjp.schedule.mapper.SystemManagerMapper.addSystemManager", systemManager);
    }

    @Override
    public int updateSystemManager(SystemManager systemManager) {
        return sqlSession.update("com.zjp.schedule.mapper.SystemManagerMapper.updateSystemManager", systemManager);
    }

    @Override
    public int deleteSystemManager(Integer id) {
        return sqlSession.delete("com.zjp.schedule.mapper.SystemManagerMapper.deleteSystemManager", id);
    }

    @Override
    public List<SystemManager> getSystemManagerAll() {
        return sqlSession.selectList("com.zjp.schedule.mapper.SystemManagerMapper.getSystemManagerAll");
    }

    @Override
    public SystemManager getSystemManagerByName(String name) {
        return sqlSession.selectOne("com.zjp.schedule.mapper.SystemManagerMapper.getSystemManagerByName", name);
    }
}
