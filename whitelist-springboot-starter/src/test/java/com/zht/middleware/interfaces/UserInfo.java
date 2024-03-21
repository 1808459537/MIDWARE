package com.zht.middleware.interfaces;

public class UserInfo {
    private String code;
    private String info;

    private String name;
    private Integer age;


    public UserInfo() {

    }

    public UserInfo(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public UserInfo(String name, Integer age) {
        this.code = "0000";
        this.info = "success";
        this.name = name;
        this.age = age;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
