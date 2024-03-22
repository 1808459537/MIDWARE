package com.zht.middleware.interfaces;

import com.zht.middleware.annotation.doWhiteList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @doWhiteList(key = "userId",returnJson = "{\"code\":\"404\",\"info\":\"非白名单可访问用户拦截！\"}")// 这个必须写成这个样子，JSON格式
    @RequestMapping(path = "/query" , method = RequestMethod.GET)
    public UserInfo queryInfo(@RequestParam String  userId){
        return new UserInfo("小张",18);
    }

    @RequestMapping("/test")
    public String test(){
        return "hello";
    }
}
