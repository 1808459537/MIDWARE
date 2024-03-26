package com.zht.middleware.mybatisspring;

import com.zht.middleware.mybatis.Resources;
import com.zht.middleware.mybatis.SqlSessionFactory;
import com.zht.middleware.mybatis.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Reader;


/**
 * SqlSessionFactoryBean类，实现了FactoryBean<SqlSessionFactory>和InitializingBean接口，
 * 用于创建SqlSessionFactory对象。
 */
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory> , InitializingBean {
    // 配置文件路径
    private String resource ;// 配置文件获取

    // SqlSessionFactory对象
    private SqlSessionFactory sqlSessionFactory; // 生命周期中创建


    /**
     * 获取SqlSessionFactory对象的类型。
     *
     * @return SqlSessionFactory对象的类型
     */
    @Override
    public SqlSessionFactory getObject() throws Exception {
        return sqlSessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return sqlSessionFactory.getClass();
    }


    /**
     * 创建SqlSessionFactory对象。
     *
     * @throws Exception 如果发生异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
            try{
                Reader reader = Resources.getResourceAsReader(resource);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);


            }catch (Exception e){}
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
