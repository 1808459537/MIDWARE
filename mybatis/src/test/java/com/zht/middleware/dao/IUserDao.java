package com.zht.middleware.dao;

import com.zht.middleware.po.User;

import java.util.List;

public interface IUserDao {
    User queryUserInfoById(long id);
    List<User> queryUserList();
}
