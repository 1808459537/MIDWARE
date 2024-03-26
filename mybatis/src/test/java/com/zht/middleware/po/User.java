package com.zht.middleware.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class User {

    private Long id;
    private String userId;          // 用户ID
    private String userNickName;    // 昵称
    private String userHead;        // 头像
    private String userPassword;    // 密码
    private Date createTime;        // 创建时间
    private Date updateTime;
}
