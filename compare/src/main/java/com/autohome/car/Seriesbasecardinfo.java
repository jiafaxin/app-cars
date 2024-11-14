package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class Seriesbasecardinfo {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);

        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://car.app.autohome.com.cn";

        List<Integer> seriesIds = bean.getAllSeriesIds();
        seriesIds.stream().sorted();

        String url = "/carbase/seriessummary/seriesbasecardinfo?cityid=%s&nodefaultcityid=%s&energytestab=test_e&yctgabtest=%s&seriesid=%s&model=1&funcabtest=%s&pm=%s&simpleinfoabtest=X&carpricelibabtest=%s&pluginversion=11.60.0";

        List<Integer> cityIds = Arrays.asList(110100,820100,653100,610100,500100,310100,460100); CityUtil.getAllCityIds().stream().sorted().collect(Collectors.toList());


        //全车系+城市 （北京 +上海+海口+澳门+新疆喀什+西安+重庆）+入参超测（energytestab=test_a +e+）+入参决策版（function=0/1）

        List<CompletableFuture> tasks = new ArrayList<>();

        for (String carpricelibabtest : Arrays.asList("A")) {//, "B", "C", "D"
            for (String yctgabtest : Arrays.asList("A")) {//, "B"
                for (Integer funcabtest : Arrays.asList(1)) {  //for (Integer funcabtest : Arrays.asList(0, 1)) {
                    for (Integer pm : Arrays.asList(1)) { //for (Integer pm : Arrays.asList(1, 2)) {
                        for (Integer cityId : cityIds) {
                            for (Integer seriesId : seriesIds) {
                                String fullUrl = String.format(url, cityId, cityId, yctgabtest, seriesId, funcabtest, pm, carpricelibabtest);
                                new CompareJson().exclude(
                                        "root[*].data.pricelist[*].pvitem.show.argvs",
                                        "root[*].data.pricelist[*].pvitem.click.argvs",
                                        "root[*].data.list[*].pvitem.show.argvs",
                                        "root[*].data.list[*].pvitem.click.argvs",
                                        "root[*].data.list[*].cornerinfo",
                                        "root[*].data.list[*].isupdate",
                                        "root[0].data.pricelist[2].title",
                                        "root[0].data.pricelist[2].subtitle",
                                        "root[0].data.pricelist[1].subtitle",
                                        "root[0].data.pricelist[2].linkurl",
                                        "root[*].data.list[*].linkurl",
                                        "root[*].data.list[*].subtitlehighlight",
                                        "root[*].data.energyinfo.pvitem.show.argvs",
                                        "root[*].data.energyinfo.pvitem.click.argvs",
                                        "root[*].data.pvitem.show.argvs",
                                        "root[*].data.pvitem.click.argvs"
                                ).excludeResult("人讨论", "人提问", "人晒价", "人分享", "人评价", "万人评价", "小时", "万]", "人关注","为JSONObject","10001","10002","10003").setRootNode("result.itemlist").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("=== success =================================");
    }
}
