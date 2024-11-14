package com.autohome.car.tools;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.*;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class HttpClient {
    private static AsyncHttpClient client;
    private final static Integer defaultRequestTimeout = 10000;
    private final static Integer defaultReadTimeout = 30000;
    private final static Integer defaultTimeout = 1000;
    private final static Integer connectTimeout = 10000;
    private final static String defaultCharset = "utf-8";

    static {
        getClient();
    }

    public static AsyncHttpClient getClient() {
        if (client == null) {
            DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config().setConnectTimeout(connectTimeout);
            client = asyncHttpClient(clientBuilder);
        }
        return client;
    }

    public static <T> CompletableFuture<T> getResult(String url, TypeReference<T> tr) {
        return toApiResult(get(url, tr, null, null, defaultRequestTimeout, defaultCharset));
    }

    public static <T> CompletableFuture<T> getResult(String url, TypeReference<T> tr, String charset) {
        return toApiResult(get(url, tr, null, null, defaultRequestTimeout, charset));
    }

    public static <T> CompletableFuture<T> getResult(String url, TypeReference<T> tr, HashMap<String, String> headerparam) {
        return toApiResult(get(url, tr, null, headerparam, defaultRequestTimeout, defaultCharset));
    }

    public static <T> CompletableFuture<T> toApiResult(CompletableFuture<HttpResult<T>> result) {
        return result.thenApply(x -> {
                    if (x.getStatusCode() < 400) {
                        return x.getResult();
                    }
                    return null;
                }
        );
    }

    public static <T> CompletableFuture<HttpResult<T>> get(String url, TypeReference<T> tr) {
        return get(url, tr, null, null, defaultRequestTimeout, defaultCharset);
    }

    public static <T> CompletableFuture<HttpResult<T>> get(String url, TypeReference<T> tr, String charset) {
        return get(url, tr, null, null, defaultRequestTimeout, charset);
    }

    public static <T> CompletableFuture<HttpResult<T>> get(String url, TypeReference<T> tr, HashMap<String, String> headerparam) {
        return get(url, tr, null, headerparam, defaultRequestTimeout, defaultCharset);
    }

    public static <T> CompletableFuture<HttpResult<T>> get(String url, TypeReference<T> tr, List<Cookie> cookies, HashMap<String, String> headerparam, int requestTimeout, String charset) {
        BoundRequestBuilder request = getClient().prepareGet(url)
                .setRequestTimeout(requestTimeout)
                .setReadTimeout(defaultReadTimeout);
        if (cookies != null && cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                request.addCookie(cookie);
            }
        }
        if (headerparam != null && headerparam.size() > 0) {
            for (Map.Entry<String, String> entry : headerparam.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return execute(request, tr, charset);
    }

    static <T> CompletableFuture<HttpResult<T>> execute(BoundRequestBuilder request, TypeReference<T> tr, String charset) {
        return request.execute(new AsyncCompletionHandler<HttpResult<T>>() {
            @Override
            public HttpResult<T> onCompleted(Response response) throws Exception {
                HttpResult<T> httpResult = new HttpResult<T>();
                httpResult.setStatusCode(response.getStatusCode());
                if (response.getStatusCode() >= 400) {
                    httpResult.setMessage(response.getResponseBody(Charset.forName(charset)));
                    return httpResult;
                }
                T result = charset.equalsIgnoreCase("utf-8")
                        ? JsonUtil.toObject(response.getResponseBodyAsStream(), tr) //只有utf-8的支持流式
                        : JsonUtil.toObject(response.getResponseBody(Charset.forName(charset)), tr);

                httpResult.setResult(result);
                return httpResult;
            }
        }).toCompletableFuture();
    }

    public static CompletableFuture<String> getString(String url, String charset, String host) {
        BoundRequestBuilder request = getClient().prepareGet(url)
                .setRequestTimeout(30000)
                .setReadTimeout(defaultReadTimeout);
        if (StringUtils.isNotBlank(host)) {
            request.addHeader("host", "car.api.autohome.com.cn");
        }
        return request.execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) {
                return response.getResponseBody(Charset.forName(charset));
            }
        }).toCompletableFuture();
    }


    public static CompletableFuture<String> postBody(String url, Object body, HttpHeaders httpHeaders) throws Exception {
        BoundRequestBuilder request = getClient().preparePost(url);

        if (httpHeaders == null) {
            httpHeaders = new DefaultHttpHeaders();
        }
//        httpHeaders.add("Content-Type","application/json");
        request.setBody((String) body);
        request.setHeaders(httpHeaders);
        return request.execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) {
                return response.getResponseBody(Charset.forName(defaultCharset));
            }
        }).toCompletableFuture();
    }

}
