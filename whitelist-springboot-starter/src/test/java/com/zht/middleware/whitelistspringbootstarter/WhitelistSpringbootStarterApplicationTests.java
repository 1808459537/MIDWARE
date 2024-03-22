package com.zht.middleware.whitelistspringbootstarter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zht.middleware"}) // 这里必须扫描所有的包，因为中间件的生命周期不在官方框架的扫描范围内
class WhitelistSpringbootStarterApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(WhitelistSpringbootStarterApplicationTests.class, args);
    }
}
