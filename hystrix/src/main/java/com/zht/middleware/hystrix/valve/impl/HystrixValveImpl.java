package com.zht.middleware.hystrix.valve.impl;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.*;
import com.zht.middleware.hystrix.annotation.DoHystrix;
import com.zht.middleware.hystrix.valve.IValveService;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class HystrixValveImpl extends HystrixCommand<Object> implements IValveService {

    private ProceedingJoinPoint jp;
    private Method method;
    private DoHystrix doHystrix;

    public HystrixValveImpl() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GovernGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GovernKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GovernThreadPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10))
        );
    }



    @Override
    protected Object run() throws Exception {
        try{
            return jp.proceed();
        }catch (Throwable e){
            return null;
        }
    }

    @Override
    public Object access(ProceedingJoinPoint jp, Method method, DoHystrix doHystrix, Object[] args) {
        this.jp = jp;
        this.method = method;
        this.doHystrix = doHystrix;
        Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GovernGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(doHystrix.timeoutValue()));

        return this.execute();

    }

    @Override
    public Object getFallback() {
        /*
            JSON.parseObject() 方法，将返回的 JSON 字符串作为第一个参数传入，并使用 method.getReturnType() 方法获取的返回类型作为第二个参数，以指示 FastJSON 库应该将 JSON 数据解析为什么类型的对象。
         */
        return JSON.parseObject(doHystrix.returnJosn(),method.getReturnType());
    }
}
