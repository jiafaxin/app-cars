package com.autohome.app.cars.job.controller;

import com.autohome.app.cars.service.components.dealer.SpecChannelComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecChannelDto;
import com.autohome.app.cars.service.components.subsidy.SpecCitySubsidyComponent;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@Slf4j
public class HomeController {
    @GetMapping(value = {"/index", "/"})
    public String hello() {
        return "success";
    }

    @GetMapping("/log")
    public String index() {
        log.info("info-job");
        log.warn("warn-job");
        log.error("error-job");
        return "success-job";
    }


//    @Autowired
//    private SpecCitySubsidyComponent specCitySubsidyComponent;
//
//    @GetMapping("/es")
//    public void es() throws IOException {
//        specCitySubsidyComponent.refreshAll(null);
//    }
//
//    @Autowired
//    private SpecChannelComponent specChannelComponent;
//
//    @GetMapping("/channel/change")
//    public String change(int specId, int channelId) {
//        SpecChannelDto specChannelDto = specChannelComponent.get(specId).join();
//        String before = specChannelDto.toString();
//        Map<TreeMap<String, Object>, SpecChannelDto> datas = new HashMap<>();
//        specChannelDto.setChannelIdList(Lists.newArrayList(channelId));
//        datas.put(specChannelComponent.makeParam(specId), specChannelDto);
//        specChannelComponent.updateBatch(datas);
//        String after = specChannelDto.toString();
//        return before + "==>" + after;
//    }
//
//    @GetMapping("/job")
//    public String job() throws IOException {
//        specChannelComponent.refreshAll(null);
//        return "success";
//    }

}
