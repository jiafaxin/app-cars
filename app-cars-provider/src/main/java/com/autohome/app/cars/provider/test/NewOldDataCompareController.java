package com.autohome.app.cars.provider.test;

import autohome.rpc.car.app_cars.v1.carcfg.GetSpecParamConfigInfoRequest;
import autohome.rpc.car.app_cars.v1.carcfg.GetSpecParamConfigInfoResponse;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.service.common.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class NewOldDataCompareController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SeriesMapper seriesMapper;

    @GetMapping(value = "/carcfg/config/newolddatacompare", produces = "application/json;charset=utf-8")
    public String newolddatacompare(GetSpecParamConfigInfoRequest request) {
        AtomicInteger count = new AtomicInteger();
        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        try {
            for (int i = 2608; i < seriesList.size(); i++) {
                SeriesEntity seriesEntity = seriesList.get(i);
                if (seriesEntity != null) {
                    System.out.println("=======start========"+"seriesId="+ seriesEntity.getId() + " seriesName" + seriesEntity.getName());
                    GetSpecParamConfigInfoRequest.Builder newRequest = GetSpecParamConfigInfoRequest.newBuilder();
                    newRequest.setCityid(request.getCityid());
                    newRequest.setSeriesid(request.getSeriesid());
                    newRequest.setPm(request.getPm());
                    newRequest.setSite(request.getSite());
                    newRequest.setSpecids(request.getSpecids());
                    newRequest.setModel(request.getModel());
                    newRequest.setPluginversion(request.getPluginversion());
                    newRequest.setYear(request.getYear());
                    newRequest.setBjabtest(request.getBjabtest());

                    newRequest.setSeriesid(seriesEntity.getId());

                    NewSpecConfigResult oldResponse = getOldData(newRequest.build());
                    GetSpecParamConfigInfoResponse newResponse = getNewData(newRequest.build());
                    CompareUtils.compareValueNew(oldResponse, newResponse);
                    count.addAndGet(1);
                    System.out.println("=======end========"+"seriesId="+ seriesEntity.getId() + " seriesName" + seriesEntity.getName());
                    System.out.println("count="+count.get());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @GetMapping(value = "/carcfg/config/newolddatacompare2", produces = "application/json;charset=utf-8")
    public String newolddatacompare2(GetSpecParamConfigInfoRequest request) {
        System.out.println("=======start========"+"seriesId="+ request.getSeriesid());
        GetSpecParamConfigInfoRequest.Builder newRequest = GetSpecParamConfigInfoRequest.newBuilder();
        newRequest.setCityid(request.getCityid());
        newRequest.setSeriesid(request.getSeriesid());
        newRequest.setPm(request.getPm());
        newRequest.setSite(request.getSite());
        newRequest.setSpecids(request.getSpecids());
        newRequest.setModel(request.getModel());
        newRequest.setPluginversion(request.getPluginversion());
        newRequest.setYear(request.getYear());
        newRequest.setBjabtest(request.getBjabtest());

        NewSpecConfigResult oldResponse = getOldData(newRequest.build());
        GetSpecParamConfigInfoResponse newResponse = getNewData(newRequest.build());
        CompareUtils.compareValueNew(oldResponse, newResponse);
        System.out.println("=======end========"+"seriesId="+ request.getSeriesid());
        return "ok";
    }

    private NewSpecConfigResult getOldData(GetSpecParamConfigInfoRequest request){
        //http://cars.app.autohome.com.cn/carcfg/config/newspeccompare?pluginversion=11.62.5&specids=&site=1&model=1&pm=2&seriesid=6962&version=11.62.5&cityid=110100
        StringBuffer pathBuffer = new StringBuffer("http://cars.app.autohome.com.cn/carcfg/config/newspeccompare");
        pathBuffer.append("?pluginversion=").append(request.getPluginversion());
        pathBuffer.append("&specids=").append(request.getSpecids());
        pathBuffer.append("&site=").append(request.getSite());
        pathBuffer.append("&model=").append(request.getModel());
        pathBuffer.append("&pm=").append(request.getPm());
        pathBuffer.append("&seriesid=").append(request.getSeriesid());
        pathBuffer.append("&version=").append(request.getPluginversion());
        pathBuffer.append("&cityid=").append(request.getCityid());
        System.out.println("old path="+pathBuffer.toString());
        NewSpecConfigResult oldResponse = restTemplate.getForObject(pathBuffer.toString(), NewSpecConfigResult.class);
        return oldResponse;
    }

    private GetSpecParamConfigInfoResponse getNewData(GetSpecParamConfigInfoRequest request){
        StringBuffer pathBuffer = new StringBuffer("http://car-app3.mesh-thallo.autohome.com.cn/carcfg/config/newspeccompare");
        pathBuffer.append("?pluginversion=").append(request.getPluginversion());
        pathBuffer.append("&specids=").append(request.getSpecids());
        pathBuffer.append("&site=").append(request.getSite());
        pathBuffer.append("&model=").append(request.getModel());
        pathBuffer.append("&pm=").append(request.getPm());
        pathBuffer.append("&seriesid=").append(request.getSeriesid());
        pathBuffer.append("&version=").append(request.getPluginversion());
        pathBuffer.append("&cityid=").append(request.getCityid());
        String response = restTemplate.getForObject(pathBuffer.toString(), String.class);
        System.out.println("new path="+pathBuffer.toString());
        GetSpecParamConfigInfoResponse newResponse = MessageUtil.toMessage(response, GetSpecParamConfigInfoResponse.class);
        return newResponse;
    }

}
