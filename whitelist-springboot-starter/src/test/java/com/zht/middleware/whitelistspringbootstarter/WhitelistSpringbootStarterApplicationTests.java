package com.zht.middleware.whitelistspringbootstarter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zht.middleware.*"})
class WhitelistSpringbootStarterApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(WhitelistSpringbootStarterApplicationTests.class, args);
    }

}
