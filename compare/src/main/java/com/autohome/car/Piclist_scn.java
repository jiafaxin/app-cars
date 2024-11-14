package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.ColorComponent;
import com.autohome.app.cars.service.components.car.dtos.ColorStatisticsDto;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class Piclist_scn {


    private static ColorComponent colorComponent;

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        colorComponent = ac.getBean(ColorComponent.class);
        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://car-app2.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();
//        外观
//        pic(seriesIds, od, nd,1,0);

        //细节
//        pic(seriesIds, od, nd,12,0);

        //中控
        pic(seriesIds, od, nd,10,1);

        //座椅
//        pic(seriesIds, od, nd,3,1);
//
//
//        //外观
//        color(seriesIds, od, nd,1,0);
//
//        //细节
//        color(seriesIds, od, nd,12,0);
//
//        //中控
//        color(seriesIds, od, nd,10,1);
//
//        //座椅
//        color(seriesIds, od, nd,3,1);
    }

    public static void pic(List<Integer> seriesIds, String od, String nd,int categoryid,int isinner) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=%s&seriesid=%s&pagesize=60&colorid=%s&pageindex=1&reqpicgroup=1&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            List<ColorStatisticsDto> colors = colorComponent.getColors(isinner==0?1:2,seriesId,0,Arrays.asList(categoryid)).join();
            for (ColorStatisticsDto color : colors) {
                String fullUrl = String.format(url, categoryid, isinner, seriesId,color.getId());
                new CompareJson().exclude(
                        "root[*].dealername",
                        "root[*].shareurl",
                        "root[*].membername",
                        "root[*].locationid"
                ).setRootNode("result.piclist").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }

        }
        System.out.println("=== success =================================");
    }



    public static void color(List<Integer> seriesIds, String od, String nd,int categoryid,int isinner) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=%s&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String fullUrl = String.format(url, categoryid, isinner, seriesId);
            new CompareJson().exclude(
                    "root[*].sort"
            ).setRootNode("result.colorlist").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
        }
        System.out.println("=== success =================================");
    }
}
