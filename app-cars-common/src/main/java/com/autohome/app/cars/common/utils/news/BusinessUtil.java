package com.autohome.app.cars.common.utils.news;

import com.autohome.app.cars.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BusinessUtil {

    public static Map<Integer, String> R_TYPE_MAP = new HashMap<>();

    static {
        R_TYPE_MAP.put(1, "010180130010025");
        R_TYPE_MAP.put(3, "010180020010092");
        R_TYPE_MAP.put(5, "010240130370011");
        R_TYPE_MAP.put(12, "010250130020037");
        R_TYPE_MAP.put(13, "010250130120087");
        R_TYPE_MAP.put(14, "010250020020004");
        R_TYPE_MAP.put(66, "010240020030008");
        R_TYPE_MAP.put(610110, "010240131350001");
        R_TYPE_MAP.put(700112, "010180131020030");
    }

    /**
     * 对协议追加ext参数
     * @param scheme
     * @return
     */
    public static String appendExtToScheme(String scheme,String ext) {
        if (StringUtils.isNotEmpty(scheme) && StringUtils.isNotEmpty(ext)) {
            scheme += (scheme.indexOf("?") > 0 ? "&ext=" : "?ext=") + Encodes.urlEncode(ext);
        }
        return scheme;
    }


}