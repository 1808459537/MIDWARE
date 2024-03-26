package com.zht.middleware.mybatisspring.dao;

import com.zht.middleware.mybatisspring.po.User;

public interface IUserDao {
    User queryUserInfoById(Long id);
}
