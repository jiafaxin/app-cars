package com.autohome.app.cars.service.services.dtos;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/14 19:34
 */
@Data
public class SubsidyTradeInConfig {
    private String h5Url;
    private List<PartConfig> partConfigList;

    @Data
    public static class PartConfig {
        private int partId;
        private String partName;
        private List<Integer> seriesIdList;
    }

    public static SubsidyTradeInConfig decode(String json) {
        if (StringUtils.hasLength(json)) {
            return JSONObject.parseObject(json, SubsidyTradeInConfig.class);
        }
        return new SubsidyTradeInConfig();
    }

}
