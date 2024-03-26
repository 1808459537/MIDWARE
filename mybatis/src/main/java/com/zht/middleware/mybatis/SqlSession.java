package com.zht.middleware.mybatis;

import java.util.List;

public interface SqlSession {
    <T> T selectOne(String statement);

    <T> T selctOne(String statement ,Object parameters);

    <T> List<T> selectList(String statement);

    <T> List<T> selectList(String statement , Object parameters);

    void close();
}
