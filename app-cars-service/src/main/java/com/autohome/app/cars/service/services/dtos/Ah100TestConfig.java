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
public class Ah100TestConfig {

    private static final Logger logger = LoggerFactory.getLogger(Ah100TestConfig.class);

    int isioen;
    List<Integer> seriesids = new ArrayList<>();

    public static Ah100TestConfig createFromJson(String json){
        if(StringUtils.isBlank(json))
            return null;
        try {
            return JsonUtil.toObject(json,Ah100TestConfig.class);
        }catch (Exception e){
            logger.error("Ah100TestConfig 序列化失败",e);
            return null;
        }

    }

}
