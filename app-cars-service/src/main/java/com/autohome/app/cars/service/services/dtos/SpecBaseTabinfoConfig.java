package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Data
public class SpecBaseTabinfoConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpecBaseTabinfoConfig.class);

    @JsonProperty("stopsale")
    private List<TabItemDTO> stopsale;
    @JsonProperty("onsale")
    private List<TabItemDTO> onsale;
    @JsonProperty("onsale_ab")
    private List<TabItemDTO> onsaleAb;
    @JsonProperty("stopsale_ab")
    private List<TabItemDTO> stopsaleAb;
    @JsonProperty("stopsale_ab_11650")
    private List<TabItemDTO> stopsaleAb_11650;
    @JsonProperty("stopsale_11650")
    private List<TabItemDTO> stopsale_11650;


    @Data
    public static class TabItemDTO {
        private String tabtitle;
        private String taburl;
        private int typeid;
        private int hide;
    }


    public static SpecBaseTabinfoConfig createFromJson(String json) {
        if (StringUtils.isBlank(json))
            return null;
        try {
            return JsonUtil.toObject(json, SpecBaseTabinfoConfig.class);
        } catch (Exception e) {
            logger.error("SpecBaseTabinfoConfig 序列化失败", e);
            return null;
        }
    }

}
