package com.autohome.app.cars.common.utils;

import com.autohome.app.cars.common.httpclient.PostType;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * crated by shicuining 2021/1/8
 */
public class HttpClientAsync {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientAsync.class);

    private static AsyncHttpClient client;

    static {
        getClient();
    }

    public static AsyncHttpClient getClient() {
        if (client == null) {
            DefaultAsyncHttpClientConfig.Builder clientBuilder =
                    Dsl.config()
                            .setConnectTimeout(1000)
                            .setIoThreadsCount(100)
                            .setPooledConnectionIdleTimeout(6000)
                            .setConnectionTtl(1000)
                            .setMaxConnections(1000)
                            .setMaxConnectionsPerHost(1000);
            client = asyncHttpClient(clientBuilder);
        }
        return client;
    }

    public static <T> ListenableFuture<T> get(String url, TypeReference<T> tr, List<Cookie> cookies, Map<String, String> headerparam, int requestTimeout, int readTimeout, String charset) {
        BoundRequestBuilder request = getClient().prepareGet(url)
                .setRequestTimeout(requestTimeout)
                .setReadTimeout(readTimeout);
        if(cookies!=null && cookies.size()>0) {
            for (Cookie cookie : cookies) {
                request.addCookie(cookie);
            }
        }
        if (headerparam != null && headerparam.size()>0) {
            for (Map.Entry<String, String> entry : headerparam.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(request,charset,tr);
    }


    public static <T> ListenableFuture<T> post(String url, PostType postType, Object body, TypeReference<T> tr, Map<String, String> headerparam, int requestTimeout, int readTimeout, String charset) {
        BoundRequestBuilder request = getClient().preparePost(url)
                .setRequestTimeout(requestTimeout)
                .setReadTimeout(readTimeout);

        if (headerparam == null) {
            headerparam = new LinkedHashMap<>();
        }

        switch (postType) {
            case JSON -> {
                headerparam.put("Content-Type", "application/json");
                if(body instanceof String){

                }else {
                    request.setBody(JsonUtil.toString(body));
                }
            }
            case X_WWW_FORM_URLENCODED -> {
                Assert.isTrue(isMapOfStringString(body),"PostType = X_WWW_FORM_URLENCODED 时，PostBody类型应该为Map<String,String>");
                headerparam.put("Content-Type", "application/x-www-form-urlencoded");
                Map<String, String> map = (Map<String, String>) body;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    request.addFormParam(entry.getKey(), entry.getValue());
                }
            }
        }


        if (headerparam != null && headerparam.size() > 0) {
            for (Map.Entry<String, String> entry : headerparam.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return execute(request, charset, tr);
    }

    private static boolean isMapOfStringString(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            if (!map.isEmpty() && map.keySet().iterator().next() instanceof String && map.values().iterator().next() instanceof String) {
                return true;
            }
        }
        return false;
    }

    static <T> ListenableFuture<T> execute(BoundRequestBuilder request,String charset,TypeReference<T> tr) {
        return request.execute(new AsyncCompletionHandler<T>() {
            @Override
            public T onCompleted(Response response) {
                if (response.getStatusCode() != 200) {
                    logger.error("调用原接口异常", new Exception(String.format("%s@@@%s", response.getUri().getBaseUrl(), response.getStatusCode())));
                    return null;
                }
                //只有utf-8的支持流式
                if (charset.equalsIgnoreCase("utf-8"))
                    return JsonUtil.toObject(response.getResponseBodyAsStream(), tr);
                return JsonUtil.toObject(response.getResponseBody(Charset.forName(charset)), tr);
            }
        });
    }

}
