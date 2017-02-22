package com.zjp.schedule.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

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
 * Module Desc:com.zjp.schedule.config
 * User: zjprevenge
 * Date: 2017/2/19
 * Time: 12:04
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig implements TransactionManagementConfigurer {

    @Resource
    private Environment env;

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("datasource.url"));
        dataSource.setUsername(env.getProperty("datasource.username"));
        dataSource.setPassword(env.getProperty("datasource.password"));
        dataSource.setDriverClassName(env.getProperty("datasource.driverClassName"));
        dataSource.setInitialSize(env.getProperty("datasource.initialSized", Integer.class));
        dataSource.setMaxActive(env.getProperty("datasource.maxActive", Integer.class));
        dataSource.setMinIdle(env.getProperty("datasource.minIdle", Integer.class));
        dataSource.setMaxWait(env.getProperty("datasource.maxWait", Integer.class));
        dataSource.setValidationQuery(env.getProperty("datasource.validationQuery"));
        dataSource.setTimeBetweenEvictionRunsMillis(env.getProperty("datasource.timeBetweenEvictionRunsMillis", Long.class));
        dataSource.setMinEvictableIdleTimeMillis(env.getProperty("datasource.minEvictableIdleTimeMillis", Long.class));
        dataSource.setTestWhileIdle(env.getProperty("datasource.testWhileIdle", Boolean.class));
        dataSource.setTestOnBorrow(env.getProperty("datasource.testOnBorrow", Boolean.class));
        dataSource.setTestOnReturn(env.getProperty("datasource.testOnReturn", Boolean.class));
        dataSource.setPoolPreparedStatements(env.getProperty("datasource.poolPreparedStatements", Boolean.class));
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(env.getProperty("datasource.maxOpenPreparedStatements",Integer.class));
        try {
            if (dataSource != null) {
                dataSource.setFilters(env.getProperty("datasource.filters"));
                dataSource.setUseGlobalDataSourceStat(true);
                dataSource.init();
            }
        } catch (SQLException e) {
            throw new RuntimeException("load datasource error ", e);
        }
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setFailFast(true);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            //有两种配置方法，和xml的配置一样：配置mapper.xml 或者mybatis-config.xml
            sessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mappers/*"));
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSession")
    public SqlSessionTemplate sqlSessionTemplate() {
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean());
        return sessionTemplate;
    }

    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }
}
