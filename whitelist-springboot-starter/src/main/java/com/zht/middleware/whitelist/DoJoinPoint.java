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


/*
    @Aspect 注解标识该类为一个切面类，用于定义切面的相关逻辑。
    @Component 注解将该切面类声明为 Spring 容器中的一个组件，从而使得 Spring 容器能够管理和识别它。
    在类中定义了一个 Logger 对象，用于记录日志。
    使用 @Autowired 注解注入了一个名为 whiteListConfig 的字符串类型的 Bean，用于获取白名单配置。
    @Pointcut 注解定义了一个切点方法 aopPoint()，该方法用于定义一个切点，它表示被 @doWhiteList 注解标注的方法。
    @Around 注解定义了一个环绕通知方法 doRouter()，用于在目标方法执行前后添加逻辑增强。
    在环绕通知方法中，通过 ProceedingJoinPoint 对象获取了目标方法，并通过反射获取了目标方法上的 @doWhiteList 注解。
    通过 getMethod() 方法获取了目标方法的 Method 对象，并通过 getAnnotation() 方法获取了目标方法上的 @doWhiteList 注解。
    通过 getFiledValue() 方法获取了目标方法参数中的关键值。
    根据白名单配置和目标方法的关键值进行对比，如果关键值在白名单中，则执行目标方法，否则根据 returnObject() 方法返回自定义的对象。

    chatgpt生成我也不知道是否完全正确
 */

@Aspect
@Component
public class DoJoinPoint {
    private Logger logger = LoggerFactory.getLogger(DoJoinPoint.class);

    @Autowired
    private String whiteListConfig;


    // 定义一个切点 ，目的就是为了塞一个已知的方法进去，然后再代理已知的方法
    @Pointcut("@annotation(com.zht.middleware.annotation.doWhiteList)")
    public void  aopPoint(){}


    // 代理已知的方法进行逻辑增强
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
