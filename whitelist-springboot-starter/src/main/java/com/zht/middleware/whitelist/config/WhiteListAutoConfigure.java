package com.zht.middleware.whitelist.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // Bean注解

@ConditionalOnClass(WhiteListProperties.class) // 条件注解 ， 当前的路径存在这样一个类的时候 ，@Configuration 才会生效
@EnableConfigurationProperties(WhiteListProperties.class) //配置注解，实行对指定类的填充（类似于@Value 注解 ， 只不过将@Value功能拆分了）
public class WhiteListAutoConfigure {
    @Bean("whitelistConfig") //  @Bean 注解放在方法上，表示该方法将会被 Spring 容器托管，并且该方法的返回值将作为一个 Bean 注册到 Spring 容器中
    @ConditionalOnMissingBean // 条件注解 ， 当没有由这个对象产生的Bean的时候，才会注册，保证单例

    public String whiteListConfig(WhiteListProperties properties){
        return properties.getUsers();
    }
}
