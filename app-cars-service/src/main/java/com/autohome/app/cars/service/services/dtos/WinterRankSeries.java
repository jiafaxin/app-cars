package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class WinterRankSeries {

    private static final Logger logger = LoggerFactory.getLogger(WinterRankSeries.class);
    private int seriesid;
    private int specid;
    private String specname;
    private String xhurl;
    private String kwhurl;
    private String kwhvalue;
    private String xhvalue;
    private String xhrate;
    private String xhofficevalue;
    private String chargeurl;
    private String chargevalue;
    private String chargearea;

    public static List<WinterRankSeries> createFromJson(String json){
        if(StringUtils.isBlank(json))
            return new ArrayList<>();
        try {
            return JsonUtil.toObjectList(json,WinterRankSeries.class);
        }catch (Exception e){
            logger.error("WinterRankSeries 序列化失败");
            return new ArrayList<>();
        }
    }
}
