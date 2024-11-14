package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class TabCard_News {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);

        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://localhost:8881";
//        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();
        test(seriesIds, od, nd);
    }

    public static void test(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "http://localhost:8881/carext/seriessummary/tabcard?seriesid=%s&tabid=2&tabids=2&pm=1&cityid=110100&pluginversion=11.62.5";
        String oldUrl = "http://cars.app.autohome.com.cn/carstreaming/seriessummary/tabcard?tabids=2&cityid=330700&deviceid=c5abc60ef6852d4a73e7ccc774d574134d828861&seriesid=%s&model=1&pm=1&provinceid=330000&pluginversion=11.62.5";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            new CompareJson().exclude(
"root.list[*].data.carddata.pvitem.show.argvs",
                    "root.list[*].data.carddata.pvitem.click.argvs",
                    "root.list[*].data.extension.objinfo.session_id",
                    "root.righticonlist",
                    "root.buttonbtnlist"
            ).setRootNode("result").compareUrlAsync(String.format(oldUrl, seriesId), String.format(url, seriesId)).join();
        }

        System.out.println("=== success =================================");
    }
}
