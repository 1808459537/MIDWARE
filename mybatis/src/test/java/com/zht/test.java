package com.zht;

import com.alibaba.fastjson.JSON;
import com.zht.middleware.mybatis.Resources;
import com.zht.middleware.mybatis.SqlSession;
import com.zht.middleware.mybatis.SqlSessionFactory;
import com.zht.middleware.mybatis.SqlSessionFactoryBuilder;
import com.zht.middleware.po.User;
import org.junit.Test;
import java.io.Reader;
import java.util.List;

public class test {
    @Test
    public void test_queryUserInfoById() {
        String resource = "mybatis-config-datasource.xml";
        Reader reader;
        try {
            reader = Resources.getResourceAsReader(resource);
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);

            SqlSession session = sqlMapper.openSession();
            try {
                User user = session.selctOne("com.zht.middleware.dao.IUserDao.queryUserInfoById",1L);
                System.out.println(JSON.toJSONString(user));
            } finally {
                session.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_queryUserList() {
        String resource = "mybatis-config-datasource.xml";
        Reader reader;
        try {
            reader = Resources.getResourceAsReader(resource);
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);

            SqlSession session = sqlMapper.openSession();
            try {
                User req = new User();
                req.setUserNickName("alen");
                List<User> userList = session.selectList("com.zht.middleware.dao.IUserDao.queryUserList", req);
                System.out.println(JSON.toJSONString(userList));
            } finally {
                session.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
