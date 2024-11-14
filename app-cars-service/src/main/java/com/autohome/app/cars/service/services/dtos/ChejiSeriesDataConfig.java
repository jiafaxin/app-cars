package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChejiSeriesDataConfig {

    private static final Logger logger = LoggerFactory.getLogger(ChejiSeriesDataConfig.class);
    private int seriesid;
    private String seriesname;
    private String content;
    private String iconurl;
    private String linkurl;
    private String browserparam;

    public static List<ChejiSeriesDataConfig> createFromJson(String json){
        if(StringUtils.isBlank(json))
            return new ArrayList<>();
        try {
            return JsonUtil.toObjectList(json,ChejiSeriesDataConfig.class);
        }catch (Exception e){
            logger.error("ChejiSeriesDataConfig 序列化失败",e);
            return new ArrayList<>();
        }
    }
}
