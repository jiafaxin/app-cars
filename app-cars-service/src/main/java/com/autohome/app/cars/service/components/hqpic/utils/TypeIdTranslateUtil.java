package com.autohome.app.cars.service.components.hqpic.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author chengjincheng
 * @date 2024/8/12
 */

public class TypeIdTranslateUtil {

    final static Map<Integer, Integer> hqPic2CpkMap = new HashMap<>();

    static {
        hqPic2CpkMap.put(2, 10);
    }

    final static Map<Integer, Integer> cpk2HqPicMap = new HashMap<>();

    static {
        cpk2HqPicMap.put(10, 2);
    }

    public static Integer getTypeId2Cpk(Integer typeIdFromHqPic) {
        Integer typeIdByCpk = hqPic2CpkMap.get(typeIdFromHqPic);
        if (Objects.nonNull(typeIdByCpk)) {
            return typeIdByCpk;
        } else {
            return typeIdFromHqPic;
        }
    }

    public static Integer getTypeId2HqPic(Integer typeIdFromCpk) {
        Integer typeIdByHqPic= cpk2HqPicMap.get(typeIdFromCpk);
        if (Objects.nonNull(typeIdByHqPic)) {
            return typeIdByHqPic;
        } else {
            return typeIdFromCpk;
        }
    }
}
