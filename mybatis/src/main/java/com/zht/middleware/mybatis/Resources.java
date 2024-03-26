package com.zht.middleware.mybatis;

import jdk.internal.util.xml.impl.Input;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    public static Reader getResourceAsReader(String resource){
        return new InputStreamReader(getResourceAsStream(resource));
    }

    private static InputStream getResourceAsStream(String resource) {
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader:classLoaders
             ) {
            InputStream inputStream = classLoader.getResourceAsStream(resource); // 用于从类路径中获取资源文件的输入流
            if (null != inputStream) {
                return inputStream;
            } }
    throw new RuntimeException();}

    /*
        ClassLoader.getSystemClassLoader() 返回系统类加载器，用于加载 Java 标准库以及应用程序的类。
        Thread.currentThread().getContextClassLoader() 返回当前线程的上下文类加载器，通常由线程的创建者设置，用于加载线程上下文中的类。
     */
    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),
                Thread.currentThread().getContextClassLoader()};
    }


}
