package com.autohome.app.cars.common.httpclient;

import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.utils.Md5Util;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * crated by shicuining 2021/1/5
 */
public class RedisKeyUtil {

    static String baseKey = "v14:";

    public static String getKey(Method method, Object[] args, AutoCache cacheAnnotation) {
        String key = baseKey + method.getDeclaringClass().getName() + ":" + method.getName();
        if (cacheAnnotation != null && StringUtils.hasLength(cacheAnnotation.v())) {
            key = key + ":" + cacheAnnotation.v();
        }
        key += Md5Util.get(args);
        return key;
    }
}
