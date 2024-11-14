package com.autohome.app.cars.common.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    public static String encode(String url){
        if(StringUtils.isBlank(url))
            return "";
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    public static String toHttps(String url) {
        if (!url.startsWith("http://")) {
            return url;
        }
        return url.replace("http://", "https://");
    }

    public static String getInsideBrowerSchemeWK(String httpScheme) {
        if (StringUtils.isBlank(httpScheme)) {
            return "";
        }
        if (!StringUtils.startsWith(httpScheme, "http://") && !StringUtils.startsWith(httpScheme, "https://")) {
            return httpScheme;
        }
        try {
            return String.format("autohome://insidebrowserwk?url=%s", URLEncoder.encode(toHttps(httpScheme), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpScheme;
    }

    public static String getFlutterUrl(String url) {
        if (StringUtils.contains(url, "flutter?url=")) {
            String encodeParam = UrlUtil.encode(StringUtils.substringAfter(url, "flutter?url="));
            return StringUtils.substringBefore(url, "flutter?url=") + "flutter?url=" + encodeParam;
        }
        return url;
    }

    public static String encodeRnAndFlutterUrl(String url){
        url = encoderUrl(url,"flutter?url=");
        url = encoderUrl(url,"rninsidebrowser?url=");
        return url;
    }

    public static String encoderUrl(String url,String str){
        if (StringUtils.contains(url, str)) {
            String encodeParam = UrlUtil.encode(StringUtils.substringAfter(url, str));
            return StringUtils.substringBefore(url, str) + str + encodeParam;
        }
        return url;
    }

    public static String getUrlParamValue(String url, String paramName) {
        String result = "";
        Pattern pXM = Pattern.compile(paramName + "=([^&]*)");
        Matcher mXM = pXM.matcher(url);
        while (mXM.find()) {
            result += mXM.group(1);
        }
        return UrlUtil.decode(result);
    }

    public static String removeParamInUrl(String url,String param) {
        try {
            String pattern = "(\\?|&)(" + param + "=[^&]*)";
            return url.replaceAll(pattern, "");
        }catch (Exception ex){
            return url;
        }
    }

    public static String addParamInExtJson(String extJson,String paramName,String paramValue){
        try {
            if (StringUtils.isEmpty(extJson)){
                extJson = "{}";
            }
            JSONObject jsonObject = JSON.parseObject(extJson);
            jsonObject.put(paramName,paramValue);
            return jsonObject.toJSONString();
        }catch (Exception ex){
            return extJson;
        }
    }

}
