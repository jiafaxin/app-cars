package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class GetSeriesListBaseInfoCard {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(GetSeriesListBaseInfoCard.class, args);
        BrandMapper bean = ac.getBean(BrandMapper.class);

        String od = "http://temp.thallo.corpautohome.com";
        String nd = "http://cars-app-http.mesh-mulan.autohome.com.cn";

        bean.getAllBrands().forEach(x -> {
            String url = "/carbase/selectcarportal/serieslistbaseinfocard?cityid=110100&cardstyleabtest=&deviceid=6bf119ac8a86694027c093c3931d7e11cb653cfc&pm=1&recommendswitch=1&pluginversion=11.58.0&brandid=" + x.getId();
            new CompareJson().exclude("root.result.serieslistrcmpvid",
                    "root.result.serieslist[*].data.pvdata.pvid",
                    "root.result.pvdata.requestpvargs[*].argvalue",
                    "root.result.serieslist[*].data.pvdata.stra",
                    "root.result.serieslist[*].data.sort",
                    "root.message",
                    "root.cdncachesecond",
                    "root.cacheable",
                    "root.result.serieslist[*].data.imgurl").compareUrlAsync(od.concat(url), nd.concat(url));
        });

        System.out.println("=== success =================================");
    }
}
