package com.autohome.app.cars.common.httpclient;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 接口实例工厂，这里主要是用于提供接口的实例对象
 * crated by shicuining 2021/1/4
 */
public class AutoHttpClientFactory<T> implements FactoryBean<T> {

    @Autowired
    StringRedisTemplate redisTemplate;

    private Class<T> interfaceType;

    @Value("${auto-httpclient.env:ONLINE}")
    AutoHttpClientEnv env;

    public AutoHttpClientFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() {
        //创建接口对应的实例，便于注入到spring容器中
        InvocationHandler handler = new AutoHttpClientProxy<>(redisTemplate, env);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType}, handler);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
