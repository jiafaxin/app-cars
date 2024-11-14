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
public class CarPriceBeanContextDto {

    private static final Logger logger = LoggerFactory.getLogger(CarPriceBeanContextDto.class);

    private String test;
    private String context;

    public static List<CarPriceBeanContextDto> createFromJson(String json){
        if(StringUtils.isBlank(json))
            return new ArrayList<>();
        try {
            return JsonUtil.toObjectList(json,CarPriceBeanContextDto.class);
        }catch (Exception e){
            logger.error("CarPriceBeanContextDto 序列化失败",e);
            return new ArrayList<>();
        }
    }
}
