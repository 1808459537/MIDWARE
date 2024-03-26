package com.zht.middleware.mybatisspring;

import com.zht.middleware.mybatis.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * MapperFactoryBean 是一个 FactoryBean 实现类，用于创建 Mapper 接口的代理对象。
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Logger logger = LoggerFactory.getLogger(MapperFactoryBean.class);


    /**
     * Mapper 接口的类型。
     */
    private Class<T> mapperInterface;

    /**
     * SqlSessionFactory 对象，用于创建 SqlSession。
     */
    private SqlSessionFactory sqlSessionFactory;


    public MapperFactoryBean(Class<T> mapperInterface, SqlSessionFactory sqlSessionFactory) {
        this.mapperInterface = mapperInterface;
        this.sqlSessionFactory = sqlSessionFactory;
    }


    /**
     * 创建 Mapper 接口的代理对象。
     *
     * @return Mapper 接口的代理对象
     * @throws Exception 创建代理对象时可能抛出的异常
     */
    @Override
    public T getObject() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> {
            logger.info("你被代理了，执行SQL操作！{}", method.getName());
            if ("toString".equals(method.getName())) return null; // 排除Object方法
            try {
                return sqlSessionFactory.openSession().selctOne(mapperInterface.getName() + "." + method.getName(), args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return method.getReturnType().newInstance();
        };

        // 代理的实际代码，三个参数，第一个类加载器，第二个是被代理的类的类对象 ，第三个就是实现了InvocationHandler接口的一个类（或者lambda表达式）
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{mapperInterface}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }
    @Override
    public boolean isSingleton() {
        return true;
    }

}
