package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/9/2 20:18
 */
@NoArgsConstructor
@Data
public class UsedCarFilterConfig {

    private List<ListDTO> list;
    private String name;
    private String parameter;
    private int type;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private String name;
        private Integer selected;
        private String typeid;
        private String value;
    }

    public static List<UsedCarFilterConfig> format(String json) {
        return JsonUtil.toObjectList(json, UsedCarFilterConfig.class);
    }
}
