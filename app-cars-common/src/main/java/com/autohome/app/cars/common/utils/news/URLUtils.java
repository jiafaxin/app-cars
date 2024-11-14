package com.autohome.app.cars.common.utils.news;

import com.autohome.app.cars.common.utils.StringUtils;

public class URLUtils {
    public URLUtils() {
    }

    public static String convertHttpToHttps(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        } else {
            String newUrl = url;
            if (url.toLowerCase().startsWith("http://")) {
                newUrl = "https:/" + url.substring(6);
            } else if (url.startsWith("://")) {
                newUrl = "https" + url;
            } else if (url.startsWith("//")) {
                newUrl = "https:" + url;
            }

            return newUrl;
        }
    }
}