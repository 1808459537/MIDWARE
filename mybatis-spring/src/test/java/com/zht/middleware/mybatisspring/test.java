package com.zht.middleware.mybatisspring;

import com.alibaba.fastjson.JSON;
import com.zht.middleware.mybatis.Resources;
import com.zht.middleware.mybatisspring.dao.IUserDao;
import com.zht.middleware.mybatisspring.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Map;

public class test {

    private Logger logger = LoggerFactory.getLogger(test.class);

    @Test
    public void test_ClassPathXmlApplicationContext() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("spring-config.xml");
        IUserDao userDao = beanFactory.getBean("IUserDao", IUserDao.class);
        User user = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

    @Test
    public void test_yml() throws Exception {
        Reader reader = Resources.getResourceAsReader("application.yml");
        Map map = (Map) new Yaml().load(reader);
        System.out.println(map.toString());
        System.out.println(((Map)((Map)map.get("mybatis")).get("datasource")).get("basePackage"));
    }


}
