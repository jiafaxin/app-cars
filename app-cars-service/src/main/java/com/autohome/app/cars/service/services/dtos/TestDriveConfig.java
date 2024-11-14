package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestDriveConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDriveConfig.class);

    @JsonProperty("is_open")
    private Integer isOpen;
    private String title;
    private String subtitle;
    private String linurl;
    @JsonProperty("city_list")
    private List<Integer> cityList;
    @JsonProperty("series_city_list")
    private List<Integer> seriesCityList;
    private Integer isvr;


    public static TestDriveConfig createFromJson(String json){
        try {
            if(StringUtils.isBlank(json))
                return null;
            return JsonUtil.toObject(json,TestDriveConfig.class);
        }catch (Exception e){
            logger.error("TestDriveConfig 序列化失败");
            return null;
        }

    }

}
