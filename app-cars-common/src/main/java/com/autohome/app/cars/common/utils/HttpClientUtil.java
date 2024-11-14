package com.autohome.app.cars.common.utils;

import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.PostType;
import com.autohome.autolog4j.common.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static HttpClient httpClient;

    static {
        ExecutorService defaultThreadPoolExecutor = new ThreadPoolExecutor(
                20,
                1000,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .executor(defaultThreadPoolExecutor)
                .build();

    }

    public static <T> CompletableFuture<T> get(String url, TypeReference<T> tr, Map<String, String> headerParam, int timeout, String charset) {
        HttpRequest.Builder request = HttpRequest.newBuilder().GET().uri(URI.create(url)).timeout(Duration.ofMillis(timeout));
        return sendAsync(request, headerParam, tr, charset);
    }


    public static <T> CompletableFuture<T> post(String url, PostType postType, Object body, TypeReference<T> tr, Map<String, String> headerParam, int timeout, String charset) {
        HttpRequest.Builder request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofMillis(timeout));

        if (headerParam == null) {
            headerParam = new LinkedHashMap<>();
        }

        switch (postType) {
            case JSON -> {
                headerParam.put("Content-Type", "application/json");
                String bodyStr = (body instanceof String)?(String)body:JsonUtil.toString(body);
                request.POST(HttpRequest.BodyPublishers.ofString(bodyStr));
            }
            case X_WWW_FORM_URLENCODED -> {
                Assert.isTrue(isMapOfStringString(body),"PostType = X_WWW_FORM_URLENCODED 时，PostBody类型应该为Map<String,String>");
                headerParam.put("Content-Type", "application/x-www-form-urlencoded");
                Map<String, String> map = (Map<String, String>) body;
                List<String> ps = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    ps.add(entry.getKey()+"="+entry.getValue());
                }
                String bodyStr = String.join("&",ps);
                request.POST(HttpRequest.BodyPublishers.ofString(bodyStr));
            }
        }
        return sendAsync(request, headerParam, tr, charset);
    }

    static <T> CompletableFuture<T> sendAsync(HttpRequest.Builder request, Map<String, String> headerParam, TypeReference<T> tr,String charset) {
        if (headerParam != null && headerParam.size() > 0) {
            for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        return httpClient.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString(Charset.forName(charset))).thenApply(response -> {
            if (response.statusCode() != 200) {
                logger.error("调用原接口异常", new Exception(String.format("%s@@@%s", response.uri().getRawPath(), response.statusCode())));
                return null;
            }
            return JsonUtil.toObject(response.body(), tr);
        });
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
}
