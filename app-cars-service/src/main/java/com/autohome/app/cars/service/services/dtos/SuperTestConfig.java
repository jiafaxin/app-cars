package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@Slf4j
public class SuperTestConfig {

    @Value("${supertestconfig:}")
    private String json;

    private String brandname;
    private int seriesid;
    private String seriesname;
    private String specname;
    private String price;
    private String specid;
    private String time;
    private String unit;
    private String milu;
    private String kmunit;
    private String linkurl;
    private int typeid;
    private String title;
    private String params="";

    public List<SuperTestConfig> get(){
        if(StringUtils.isBlank(json))
            return new ArrayList<>();
        try {
            return JsonUtil.toObjectList(json,SuperTestConfig.class);
        }catch (Exception e){
            log.error("SuperTestConfig 序列化失败",e);
            return new ArrayList<>();
        }

    }

}
