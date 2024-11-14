package com.autohome.app.cars.common.utils;

import org.apache.commons.lang3.StringUtils;

public class HttpUtils {

    public static String ToHttp(String url) {
        String uString = url;
        if (!StringUtils.isNotEmpty(uString))
            return "";
        if (url.toLowerCase().startsWith("https://")) {
            uString = "http://" + url.substring(8);
        }
        return uString;
    }

    public static String ToHttps(String url) {
        String uString = url;
        if (!StringUtils.isNotEmpty(uString))
            return "";
        if (url.toLowerCase().startsWith("http://")) {
            uString = "https://" + url.substring(7);
        }
        return uString;
    }
}
