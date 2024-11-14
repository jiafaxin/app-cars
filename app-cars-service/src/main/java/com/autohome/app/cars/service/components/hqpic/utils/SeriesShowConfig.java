package com.autohome.app.cars.service.components.hqpic.utils;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/8/18
 */
@Data
@Slf4j
public class SeriesShowConfig {

    /**
     * 0-全都不外展
     * 1-按照车系外展
     * 2-全都外展
     */
    int showType;

    List<Integer> seriesIdList;

    public static SeriesShowConfig decode(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new SeriesShowConfig();
            }
            return JsonUtil.toObject(json, SeriesShowConfig.class);
        } catch (Exception e) {
            log.error("SeriesShowConfig 序列化失败", e);
            return new SeriesShowConfig();
        }
    }
}
