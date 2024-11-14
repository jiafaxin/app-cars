package com.autohome.app.cars.service.common;

import io.lettuce.core.cluster.SlotHash;

import java.util.Arrays;
import java.util.List;

public class RedisKeyUtil {
    static List<String> slotKey = Arrays.asList("BW","AV","AO","DU","FN","BV","AW","AN","EN","FO","BQ","OO","DN","DW","GV","CN","AU","DO","DV","FI");
    static int oneSlotBase = 820;

    /**
     * 把redis key  通过{hash tag} 转为均匀分布在20个slot的key
     * @param originalKey
     * @return
     */
    public static String convertToCustomSlotKey(String originalKey) {
        int slot = SlotHash.getSlot(originalKey);
        int index = slot / oneSlotBase;
        if(index >= slotKey.size()){
            index = 1;
        }
        String hashTag = slotKey.get(index);
        return "{" + hashTag + "}" + originalKey;
    }
}
