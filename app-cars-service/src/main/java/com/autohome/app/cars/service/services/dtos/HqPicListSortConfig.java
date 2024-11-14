package com.autohome.app.cars.service.services.dtos;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/8/12
 */
@Data
public class HqPicListSortConfig {


    int typeId;

    int subTypeId;

    List<String> sortIdList;

    public static List<HqPicListSortConfig> decodeVideoVRConfigModel(String json) {
        try {
            if (json == null || json.equals("")) {
                return new ArrayList<>();
            }
            return JSON.parseObject(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
