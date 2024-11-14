package com.autohome.app.cars.provider.controller;


import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.Md5Util;
import com.autohome.app.cars.service.common.RedisConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.http.HttpTimeoutException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@Slf4j
public class ComponentController implements ApplicationContextAware {

    @Autowired
    MainDataRedisTemplate redisTemplate;

    ApplicationContext applicationContext;

    final static String RedisBaseKey = "appcar:component:";

    @Autowired
    MethodInvokeService methodInvokeService;

    /**
     * 该方法支持组件中的方法直接调用
     *
     * @param request
     * @return
     */
    @GetMapping(value = {"/v2/getComponentValue","/carplatform/getComponentValue"}, produces = "application/json;charset=UTF-8")
    public Object getComponentNewValue(HttpServletRequest request) {
        try {
            String serverName = request.getServerName();
            //限制内网访问，先域名限制，serverName包括一下域名才能访问
            if (checkServerName(serverName)) {
                log.error("非法访问");
                return new BaseModel(-1, "非法访问:" + serverName);
            }

            return methodInvokeService.invokeMethod(request);

        } catch (Exception e) {
            log.error("getValue error", e);
            return new BaseModel(-1, e.getMessage());
        }
    }

    /**
     * 根据参数生成redis调用key，有局限
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getComponentValue", produces = "application/json;charset=UTF-8")
    public BaseModel getComponentValue(HttpServletRequest request) {
        try {
            String serverName = request.getServerName();
            //限制内网访问，先域名限制，serverName包括一下域名才能访问
            if (checkServerName(serverName)) {
                log.error("非法访问");
                return new BaseModel(-1, "非法访问:" + serverName);
            }

            //获取组件名
            String name = request.getParameter("component");
            //获取除了name以外的所有参数放到treeMap
            TreeMap<String, Object> params = new TreeMap<>();
            request.getParameterMap().forEach((k, v) -> {
                if (!"component".equals(k) && !"_appid".equals(k)) {
                    try {
                        if (!StringUtils.hasLength(v[0])) {
                            return;
                        }
                        int intValue = Integer.parseInt(v[0]);
                        params.put(k, intValue);
                    } catch (NumberFormatException e) {
                        params.put(k, String.valueOf(v[0]));
                    }
                }
            });
            Object bean = applicationContext.getBean(name + "Component");
            String value = redisTemplate.opsForValue().get(getKey(bean, params));
            Object obj = JsonUtil.toObject(value, new TypeReference<>() {
            });

            return new BaseModel(obj);
        } catch (Exception e) {
            log.error("getValue error", e);
            return new BaseModel(-1, e.getMessage());
        }
    }

    private boolean checkServerName(String serverName) {
        return Stream.of("localhost", "thallo.autohome.com.cn", "mulan.autohome.com.cn", "corpautohome.com")
                .noneMatch(serverName::contains);
    }

    /**
     * 该方法需要再组件里添加Treemap参数，有局限，参考使用：/v2/getComponentValue
     *
     * @param request
     * @return
     */
    @GetMapping(value ={"/getComponentValueByMethod","/carplatform/getComponentValueByMethod"} , produces = "application/json;charset=UTF-8")
    public BaseModel getComponentValueByMethod(HttpServletRequest request) {
        try {
            String serverName = request.getServerName();
            // 限制内网访问，先域名限制，serverName包括一下域名才能访问
            if (checkServerName(serverName)) {
                log.error("非法访问");
                return new BaseModel(-1, "非法访问:" + serverName);
            }

            // 获取组件名
            String name = request.getParameter("component");
            // 获取组件名(参数中有传则取参数值，没传默认使用“get”方法)
            String requestMethodName = request.getParameter("method");
            String methodName = StringUtils.hasLength(requestMethodName) ? requestMethodName : "get";
            // 获取除了name以外的所有参数放到treeMap
            TreeMap<String, Object> params = new TreeMap<>();
            request.getParameterMap().forEach((k, v) -> {
                try {
                    if (!StringUtils.hasLength(v[0])) {
                        return;
                    }
                    int intValue = Integer.parseInt(v[0]);
                    params.put(k, intValue);
                } catch (NumberFormatException e) {
                    params.put(k, String.valueOf(v[0]));
                }
            });
            Object bean = applicationContext.getBean(name + "Component");
            Method method = bean.getClass().getDeclaredMethod(methodName, TreeMap.class);
            // 调用方法
            String value = (String) method.invoke(bean, params);
            Object obj = JsonUtil.toObject(value, new TypeReference<>() {
            });

            return new BaseModel(obj);
        } catch (Exception e) {
            if (e.getCause() instanceof InvocationTargetException) {
                log.warn("null值转换错误", e);
            } else {
                log.error("getValue error", e);
            }
            return new BaseModel(-1, e.getMessage());
        }
    }

    /**
     * 把参数转换为key
     *
     * @param params
     * @return
     */
    private String getKey(Object bean, TreeMap<String, Object> params) {
        String verion = null;
        RedisConfig redisConfig = bean.getClass().getAnnotation(RedisConfig.class);
        if (redisConfig != null) {
            verion = redisConfig.keyVersion();
        }

        String key = params == null || params.size() == 0 ? "" : Md5Util.get(params);
        key = getBaseKey(bean) + ":" + key;
        if (StringUtils.hasLength(verion)) {
            key = key + ":" + verion;
        }
        return key;
    }

    private String getBaseKey(Object bean) {
        return RedisBaseKey + bean.getClass().getName();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
