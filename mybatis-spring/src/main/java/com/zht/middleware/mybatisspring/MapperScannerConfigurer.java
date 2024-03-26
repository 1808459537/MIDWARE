package com.zht.middleware.mybatisspring;

import com.zht.middleware.mybatis.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;


import java.beans.Introspector;
import java.io.IOException;


/**
 * MapperScannerConfigurer类，实现了BeanDefinitionRegistryPostProcessor接口，
 * 用于扫描指定包路径下的Mapper接口，并注册相应的BeanDefinition。
 */
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    // 要扫描的基础包路径
    private String basePackage;

    // 注入的SqlSessionFactory对象，用于创建Mapper实例
    private SqlSessionFactory sqlSessionFactory;


    /**
     * 在Bean定义注册阶段，扫描指定包路径下的Mapper接口，并注册相应的BeanDefinition。
     *
     * @param registry BeanDefinitionRegistry对象，用于注册BeanDefinition
     * @throws BeansException 如果发生Beans异常
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try{
            // 构建包路径搜索路径  classpath*:com/zht/middleware/mybatisspring/dao/**/*.class
            String packageSearchPath = "classpath*:" + basePackage.replace('.','/') + "/**/*.class";
            // 创建资源模式解析器
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

            // 获取指定包路径下的所有资源  file [T:\JAVA\CODE\MIDWARE\mybatis-spring\target\test-classes\com\zht\middleware\mybatisspring\dao\IUserDao.class]
            Resource[] resource = resourcePatternResolver.getResources(packageSearchPath);

            for (Resource re : resource
                 ) {
                // 创建元数据读取器
                MetadataReader metadataReader = new SimpleMetadataReader(re, ClassUtils.getDefaultClassLoader());

                // 通常情况下，当Spring进行扫描并解析类路径下的Bean时，会创建ScannedGenericBeanDefinition对象来表示被扫描到的类，然后将这些对象注册到Bean工厂中，从而完成对Bean的定义和注册。
                ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);

                // 获取Bean名称，采用类名的小写形式
                String beanName = Introspector.decapitalize(ClassUtils.getShortName(beanDefinition.getBeanClassName()));
                // 设置Bean的资源和来源
                beanDefinition.setResource(re);
                beanDefinition.setSource(re);


                beanDefinition.setScope("singleton");
                // 设置Bean的构造参数，分别为Mapper接口的类名和SqlSessionFactory对象
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(sqlSessionFactory);
                // 设置Bean的类为MapperFactoryBean
                beanDefinition.setBeanClass(MapperFactoryBean.class);

                // 创建Bean定义持有者
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                // 注册BeanDefinition
                registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
            }
        }catch (Exception e){}
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    /**
     * 设置要扫描的基础包路径。
     *
     * @param basePackage 基础包路径
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 设置注入的SqlSessionFactory对象。
     *
     * @param sqlSessionFactory SqlSessionFactory对象
     */
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

}
