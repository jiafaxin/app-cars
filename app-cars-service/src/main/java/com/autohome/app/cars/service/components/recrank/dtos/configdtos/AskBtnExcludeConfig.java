package com.autohome.app.cars.service.components.recrank.dtos.configdtos;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@NoArgsConstructor
@Data
@Slf4j
public class AskBtnExcludeConfig {


    @JsonProperty("list")
    private List<Integer> list;
    @JsonProperty("isopen")
    private Integer isopen;

    public static AskBtnExcludeConfig createFromJson(String json){
        try {
            if(StringUtils.isBlank(json))
                return null;
            return JSON.parseObject(json,AskBtnExcludeConfig.class);
        }catch (Exception e){
            log.error("AskBtnExcludeConfig 序列化失败");
            return null;
        }

    }

}
