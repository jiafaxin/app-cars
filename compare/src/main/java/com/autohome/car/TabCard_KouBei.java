package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.CityUtil;
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
public class TabCard_KouBei {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);

//        String od = "http://cars.app.autohome.com.cn";
        String od = "http://localhost:8182";
        String nd = "http://localhost:8881";
//        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();
        test(seriesIds, od, nd);
    }

    public static void test(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carext/seriessummary/tabcard?seriesid=%s&tabid=10&tabids=10&pm=1&cityid=110100&pluginversion=11.62.5";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String fullUrl = String.format(url, seriesId);
            new CompareJson().exclude(
                    "root[*].data.pvitem.show.argvs",
                    "root[*].data.pvitem.click.argvs",
                    "root[*].data.viewcount",
                    "root[*].data.commentcount",
                    "root[*].data.helpfulcount",
                    ""
            ).setRootNode("result.list").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
        }

        System.out.println("=== success =================================");
    }
}
