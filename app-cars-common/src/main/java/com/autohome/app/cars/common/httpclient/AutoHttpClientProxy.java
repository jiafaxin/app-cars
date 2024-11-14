package com.autohome.app.cars.common.httpclient;

import com.autohome.app.cars.common.httpclient.annotation.*;
import com.autohome.app.cars.common.utils.HttpClientAsync;
import com.autohome.app.cars.common.utils.HttpClientUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.ListenableFuture;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.net.http.HttpTimeoutException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 动态代理，需要注意的是，这里用到的是JDK自带的动态代理，代理对象只能是接口，不能是类
 * crated by shicuining 2021/1/4
 */


@Slf4j
public class AutoHttpClientProxy<T> implements InvocationHandler {

    private StringRedisTemplate redisTemplate;

    private AutoHttpClientEnv env;


    public AutoHttpClientProxy(StringRedisTemplate redisTemplate,AutoHttpClientEnv env) {
        this.redisTemplate = redisTemplate;
        this.env = env;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return CompletableFuture.completedFuture(method.invoke(this, args));
        }
        return getData(method, args);
    }

    private CompletableFuture<Object> getData(Method method, Object[] args) {
        AutoCache cacheAnnotation = method.getAnnotation(AutoCache.class);
        String key = cacheAnnotation == null ? "" : RedisKeyUtil.getKey(method, args, cacheAnnotation);

        Type returnType = ((ParameterizedType) method.getAnnotatedReturnType().getType()).getActualTypeArguments()[0];
        TypeReference<Object> typeReference = new TypeReference<Object>() {
            @Override
            public Type getType() {
                return returnType;
            }
        };

        if (cacheAnnotation != null) {
            String json = redisTemplate.opsForValue().get(key);
            if (StringUtils.hasLength(json)) {
                return CompletableFuture.completedFuture(JsonUtil.toObject(json, typeReference));
            }
        }

        String url;
        String charset = "";
        int timeout = 500;

        Map<String, String> headerparam = getHeaders(method, args);
        CompletableFuture<Object> result;
        AutoGet autoGet = method.getAnnotation(AutoGet.class);

        if (autoGet != null) {
            url = switch (this.env) {
                case ONLINE -> autoGet.online();
                case BETA -> autoGet.beta();
                default -> autoGet.dev();
            };
            if(StringUtils.hasLength(autoGet.authorization())){
                headerparam.put("Authorization",autoGet.authorization());
            }
            if(StringUtils.hasLength(autoGet.userAgentHeader())){
                headerparam.put("User-Agent",autoGet.userAgentHeader());
            }
            charset = autoGet.charset();
            timeout = autoGet.timeout();
            url = getUrl(url, method, args);
            result = HttpClientUtil.get(url, typeReference, headerparam, timeout, charset);
        } else {
            AutoPost autoPost = method.getAnnotation(AutoPost.class);
            if (autoPost != null) {
                url = switch (this.env) {
                    case ONLINE -> autoPost.online();
                    case BETA -> autoPost.beta();
                    default -> autoPost.dev();
                };
                if(StringUtils.hasLength(autoPost.authorization())){
                    headerparam.put("Authorization",autoPost.authorization());
                }
                if(StringUtils.hasLength(autoPost.userAgentHeader())){
                    headerparam.put("User-Agent",autoPost.userAgentHeader());
                }
                charset = autoPost.charset();
                timeout = autoPost.timeout();
                url = getUrl(url, method, args);
                result = HttpClientUtil.post(url, autoPost.type(), getPostBody(method, args), typeReference, headerparam, timeout, charset).toCompletableFuture();
            } else {
                throw new RuntimeException("必须设置AutoGet或AutoPost注解");
            }
        }

        String finalUrl = url;
        return result.thenApply(urlResult -> {
            if (cacheAnnotation != null) {
                redisTemplate.opsForValue().set(key, JsonUtil.toString(urlResult), cacheAnnotation.liveTime(), TimeUnit.SECONDS);
            }
            return urlResult;
        }).exceptionally(e -> {
            if (e.getCause() instanceof HttpTimeoutException) {
                log.warn("调用原接口超时" + finalUrl, e);
            } else {
                log.error("调用原接口报错" + finalUrl, e);
            }
            return null;
        });
    }

    private String getUrl(String baseUrl, Method method, Object[] args) {
        Parameter[] params = method.getParameters();
        if (params == null || params.length == 0)
            return baseUrl;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = params[i];
            if(parameter.getAnnotation(CookieParameter.class)!=null||parameter.getAnnotation(HeaderParameter.class)!=null)
                continue;

            baseUrl = baseUrl.replace(String.format("${%s}", parameter.getName()), args[i].toString());
        }
        return baseUrl;
    }

    private  Map<String, String> getHeaders(Method method, Object[] args) {
        Map<String, String> headerparam = new LinkedHashMap<>();
        Parameter[] params = method.getParameters();
        if (params == null || params.length == 0)
            return headerparam;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = params[i];
            if (parameter.getAnnotation(HeaderParameter.class) == null) {
                continue;
            }
            headerparam.put(parameter.getName(), args[i].toString());
        }
        return headerparam;
    }

    Object getPostBody(Method method, Object[] args) {
        Parameter[] params = method.getParameters();
        if (params == null || params.length == 0)
            return null;
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = params[i];
            if (parameter.getAnnotation(PostBody.class) == null) {
                continue;
            }
            return args[i];
        }
        return null;
    }




}
