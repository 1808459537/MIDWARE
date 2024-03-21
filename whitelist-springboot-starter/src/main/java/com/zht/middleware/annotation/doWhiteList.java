package com.zht.middleware.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited  // 被该注解打上的类如果被继承了，那么继承者也会被打上该注解
public @interface doWhiteList {
    String key() default "";
    String returnJson() default "";
}
