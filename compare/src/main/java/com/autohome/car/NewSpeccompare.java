package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class NewSpeccompare {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SpecMapper specMapper = ac.getBean(SpecMapper.class);
        SeriesMapper seriesMapper = ac.getBean(SeriesMapper.class);
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());
        List<Integer> specIds = specAll.stream().filter(i -> i.getParamIsShowByState() == 1).map(i -> i.getId()).collect(Collectors.toList());
        String od ="http://cars.app.autohome.com.cn";
        String nd ="http://car-app3.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = getSeriesList(seriesMapper);
//        testConditionList(seriesIds,od,nd);
        testAttentionspecinfo(seriesIds,od,nd);
//        testToolboxentry(seriesIds,od,nd);
//        testSpecConditionList(specIds,od,nd);
    }

public static List<Integer> getSeriesList(SeriesMapper seriesMapper){
    List<Integer> seriesIdsParamIsShow = seriesMapper.getSeriesParamIsShowAll();
    List<Integer> allStopSeriesIsImageSpec = seriesMapper.getAllStopSeriesIsImageSpec();
    List<Integer> seriesList =new ArrayList<>();
    seriesMapper.getAllSeries().forEach(dto->{
        int paramIsShow = 0;
        if (dto.getState() <= 30) {
            paramIsShow = seriesIdsParamIsShow.contains(dto.getId()) ? 1 : 0;
        } else if (dto.getState() == 40) {
            paramIsShow = !allStopSeriesIsImageSpec.contains(dto.getId()) ? 1 : 0;
        }
        if(paramIsShow==1){
            seriesList.add(dto.getId());
        }
    });
    seriesList.stream().sorted();
    return seriesList;
}

    public static void testConditionList(List<Integer> seriesIds, String od, String nd){
        String url = "/carcfg/config/newspeccompare?pm=1&model=0&cityid=110100&specids=&site=1&version=11.62.0&pluginversion=11.62.0&seriesid=";
        int count = 0;
        for (Integer seriesid:seriesIds){
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String furl = url+seriesid;
            new CompareJson().exclude(
                    "root.configitems",
                    "root.cpsinfo",
                    "root.filterlist",
                    "root.footaskpriceinfo",
                    "root.hasmore",
                    "root.paramitems",
                    "root.selectconfig",
                    "root.seriesids",
                    "root.specinfo",
                    "root.usedinfo",
                    "root.titlelist",
                    "root.datalist",
                    "root.deletespecids",
                    "root.deletetip",
                    "root.attentionspecinfo",
                    "root.toolboxentry",
                    "root.mustseelist"



            ).setRootNode("result").compareUrlAsync(od.concat(furl), nd.concat(furl)).join();
        }
        System.out.println("=== success =================================");
    }

    public static void testSpecConditionList(List<Integer> specIds, String od, String nd){
        String url = "/carcfg/config/newspeccompare?pm=1&model=0&cityid=110100&seriesid=&site=2&version=11.62.0&pluginversion=11.62.0&specids=";
        int count = 0;
        for (Integer specid:specIds){
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String furl = url+specid;
            new CompareJson().exclude(
                    "root.configitems",
                    "root.cpsinfo",
                    "root.filterlist",
                    "root.footaskpriceinfo",
                    "root.hasmore",
                    "root.paramitems",
                    "root.selectconfig",
                    "root.seriesids",
                    "root.specinfo",
                    "root.usedinfo",
                    "root.titlelist",
                    "root.datalist",
                    "root.deletespecids",
                    "root.deletetip",
                    "root.attentionspecinfo",
                    "root.toolboxentry",
                    "root.mustseelist"



            ).setRootNode("result").compareUrlAsync(od.concat(furl), nd.concat(furl)).join();
        }
        System.out.println("=== success =================================");
    }

    public static void testAttentionspecinfo(List<Integer> seriesIds, String od, String nd){
        String url = "/carcfg/config/newspeccompare?pm=1&model=0&cityid=110100&specids=&site=1&version=11.62.0&pluginversion=11.62.0&seriesid=";
        int count = 0;
        for (Integer seriesid:seriesIds){
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String furl = url+seriesid;
            new CompareJson().exclude(
                    "root.configitems",
                    "root.cpsinfo",
                    "root.filterlist",
                    "root.footaskpriceinfo",
                    "root.hasmore",
                    "root.paramitems",
                    "root.selectconfig",
                    "root.seriesids",
                    "root.specinfo",
                    "root.usedinfo",
                    "root.titlelist",
                    "root.datalist",
                    "root.deletespecids",
                    "root.deletetip",
                    "root.conditionlist",
                    "root.toolboxentry",
                    "root.mustseelist"



            ).setRootNode("result").compareUrlAsync(od.concat(furl), nd.concat(furl)).join();
        }
        System.out.println("=== success =================================");
    }

    public static void testToolboxentry(List<Integer> seriesIds, String od, String nd){
        String url = "/carcfg/config/newspeccompare?pm=1&model=0&cityid=110100&specids=&site=1&version=11.62.0&pluginversion=11.62.0&seriesid=";
        int count = 0;
        for (Integer seriesid:seriesIds){
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String furl = url+seriesid;
            new CompareJson().exclude(
                    "root.configitems",
                    "root.cpsinfo",
                    "root.filterlist",
                    "root.footaskpriceinfo",
                    "root.hasmore",
                    "root.paramitems",
                    "root.selectconfig",
                    "root.seriesids",
                    "root.specinfo",
                    "root.usedinfo",
                    "root.titlelist",
                    "root.datalist",
                    "root.deletespecids",
                    "root.deletetip",
                    "root.conditionlist",
                    "root.attentionspecinfo",
                    "root.mustseelist",
                    "root.toolboxentry.entrypvdata.show",
                    "root.toolboxentry.list[0].pvdata.show",
                    "root.toolboxentry.list[1].pvdata.show",
                    "root.toolboxentry.list[2].pvdata.show",
                    "root.toolboxentry.list[0].title",
                    "root.toolboxentry.list[1].title",
                    "root.toolboxentry.list[2].title"



            ).setRootNode("result").compareUrlAsync(od.concat(furl), nd.concat(furl)).join();
        }
        System.out.println("=== success =================================");
    }

}
