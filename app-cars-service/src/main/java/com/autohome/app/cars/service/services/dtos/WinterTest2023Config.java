package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 冬测入口开关配置
 * @date : 2024/3/14 15:52
 */
@Data
public class WinterTest2023Config {
    private static final Logger logger = LoggerFactory.getLogger(WinterTest2023Config.class);
    int isopen;
    List<Integer> excludelist = new ArrayList<>();

    public static WinterTest2023Config createFromJson(String json) {
        if (StringUtils.isBlank(json))
            return null;
        try {
            return JsonUtil.toObject(json, WinterTest2023Config.class);
        } catch (Exception e) {
            logger.error("WinterTest2023Config 序列化失败", e);
            return null;
        }
    }
}
