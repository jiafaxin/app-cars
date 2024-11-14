package com.autohome.app.cars.job.controller;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.service.components.che168.SeriesYearCityPriceComponent;
import com.autohome.app.cars.service.services.RecRankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author gaoqiri
 * @date 2024/10/31
 */
@RestController
@Slf4j
public class ComponentRetryController {

    @Autowired
    private SeriesYearCityPriceComponent seriesYearCityPriceComponent;

    @GetMapping(value = "/SeriesYearCityPrice/retry")
    public JSONObject updateSaleRank(@RequestParam(value = "seriesIds") String seriesIds) {
        JSONObject result = new JSONObject();
        String[] seriesIdList = StringUtils.split(seriesIds,",");
        if(null == seriesIdList){
            result.put("returncode", -1);
            result.put("message", "seriesIds is null");
            return result;
        }
        for(String seriesId : seriesIdList){
            seriesYearCityPriceComponent.refreshOne((x)->{},Integer.parseInt(seriesId));
        }

        result.put("returncode", 0);
        result.put("message", "success");
        return result;
    }
}
