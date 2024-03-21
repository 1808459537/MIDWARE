package com.zht.middleware.whitelist;
import com.alibaba.fastjson.JSON;
import com.zht.middleware.annotation.doWhiteList;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DoJoinPoint {
    private Logger logger = LoggerFactory.getLogger(DoJoinPoint.class);

    @Autowired
    private String whiteListConfig;

    @Pointcut("@annotation(com.zht.middleware.annotation.doWhiteList)")
    public void  aopPoint(){}

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint jp) throws Throwable {
        Method method = getMethod(jp);

        doWhiteList whiteList = method.getAnnotation(doWhiteList.class);
        String keyValue = getFiledValue(whiteList.key(), jp.getArgs());
        logger.info("middleware whitelist handler method：{} value：{}", method.getName(), keyValue);

        if(null == keyValue || "".equals(keyValue))return jp.proceed();
        String [] split = whiteListConfig.split(",");


        //对比过滤

        for (String s :
                split) {
            if(s.equals(keyValue)){
                return jp.proceed();
            }
        }

        return returnObject(whiteList, method);
    }

    private Object returnObject(doWhiteList whiteList, Method method) throws IllegalAccessException, InstantiationException {
        Class<?> returnType = method.getReturnType();
        String returnJson = whiteList.returnJson();
        if("".equals(returnJson))
            return returnType.newInstance();
        return JSON.parseObject(returnJson, returnType); // JSON转java对象
    }

    private String getFiledValue(String key, Object[] args) {
        String file = null;
        for (Object arg:
             args) {
            try{
                if(file == null || "".equals(file)){
                    file = BeanUtils.getProperty(arg , key);
                }else break;

            }catch (Exception e){
                if(args.length == 1)
                    return args[0].toString();
            }
        }
        return file;
    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;

        return jp.getTarget().getClass().getMethod(methodSignature.getName(),methodSignature.getParameterTypes());
    }
}
