package com.autohome.app.cars.common.carconfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpecElectric {
    public static Map<Integer, String> dicExcludePEVCarConfig = new LinkedHashMap<Integer, String>() {
        {
            put(9, "发动机电子防盗");
            put(25, "前桥限滑差速器/差速锁");
            put(26, "中央差速器锁止功能");
            put(27, "后桥限滑差速器/差速锁");
            put(106, "发动机启停技术");
            put(139, "流媒体车内后视镜");
        }
    };
}
