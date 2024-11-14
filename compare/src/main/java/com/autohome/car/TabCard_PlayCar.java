package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.owner.SeriesPlayCarComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class TabCard_PlayCar {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesPlayCarComponent service;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(TabCard_PlayCar.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        SeriesPlayCarComponent service = ac.getBean(SeriesPlayCarComponent.class);

        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://localhost:8881";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();

//        List<Integer> tempList = new ArrayList<>() {{
//            add(7785);
//            add(7306);
//            add(364);
//            add(827);
//            add(873);
//        }};
//
//        tempList.forEach(seriesId -> {
//            service.refreshOne(x -> {
//            }, seriesId, 120100);
//
//            ThreadUtil.sleep(100);
//            test(tempList, od, nd);
//        });

        test(seriesIds, od, nd);
    }

    public static void test(List<Integer> seriesIds, String od, String nd) {
        // 城市 （北京+澳门+喀什+西安+重庆+上海+海口）
        List<Integer> cityIds = Arrays.asList(820100, 653100, 610100, 500100, 310100, 460100, 110100);
        int count = 0;
        String url = "/carext/seriessummary/tabcard?seriesid=%s&tabid=11&tabids=11&pm=1&cityid=%s&pluginversion=11.62.5";
        for (Integer cityId : cityIds) {
            for (Integer seriesId : seriesIds) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, seriesId, cityId);
                new CompareJson().exclude(
                        "root[*].data.extension.objinfo.title", //两次标题可能不一样
                        "root[*].data.carddata.cardinfo.title",
                        "root[*].data.carddata.cardinfo.taginfo[*].text",
                        ""
                ).setRootNode("result.list").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }
        }

        System.out.println("=== success =================================");
    }
}

