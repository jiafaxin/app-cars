package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class TabCard_UsedCar {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(TabCard_UsedCar.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);

        String od = "http://cars.app.autohome.com.cn";
//        String od = "http://localhost:8182";
        String nd = "http://localhost:8881";
//        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();
        test(seriesIds, od, nd);
    }

    public static void test(List<Integer> seriesIds, String od, String nd) {
        // 城市 （北京+澳门+喀什+西安+重庆+上海+海口）
        List<Integer> cityIds = Arrays.asList(820100, 653100, 610100, 500100, 310100, 460100, 110100);
        int count = 0;
        String url = "/carstreaming/seriessummary/tabcard?seriesid=%s&tabid=18&tabids=18&pm=1&cityid=%s&pluginversion=11.65.5";
        for (Integer seriesId : seriesIds) {
            for (Integer cityId : cityIds) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, seriesId, cityId);
                new CompareJson().exclude(
                        "root[*].data.crosscut.pvitem.show",
                        "root[*].data.pvitem.click.argvs.linkid",
                        "root[0].data.crosscut.pvitem.argvs",
                        "root[0].data.hadgetext",
                        "root[0].data.pvitem.show.argvs",
                        "root[0].data.pvitem.click.argvs",
                        "root[*].data.submark",
                        "root[*].id",
                        "root[0].data.hadgevalue" //差别一点点，先忽略
                ).setRootNode("result.list").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }
        }

        System.out.println("=== success =================================");
    }
}
