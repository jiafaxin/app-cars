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

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class Seriesbasecardinfo_dk {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);

        String od = "http://cars.app.autohome.com.cn";
//        String nd = "http://localhost:8881";
        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();

//        testAll(seriesIds, od, nd);
        testBeijing(seriesIds, od, nd);
    }

    public static void testBeijing(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carbase/seriessummary/seriesbasecardinfo?cityid=%s&nodefaultcityid=%s&energytestab=test_f&yctgabtest=%s&seriesid=%s&model=1&funcabtest=%s&pm=%s&simpleinfoabtest=X&carpricelibabtest=%s&pluginversion=11.59.0";
        for (String carpricelibabtest : Arrays.asList("A", "B", "C", "D")) {
            for (String yctgabtest : Arrays.asList("A", "B")) {
                for (Integer funcabtest : Arrays.asList(0, 1)) {
                    for (Integer seriesId : seriesIds) {
                        count++;
                        if (count % 1000 == 0) {
                            System.out.println("当前请求数量：" + count);
                        }
                        String fullUrl = String.format(url, 110100, 110100, yctgabtest, seriesId, funcabtest, 1, carpricelibabtest);
                        new CompareJson().exclude(
                                "root.dealerpriceinfo",
                                "root.edgehyperlink.bottomlistjm",
                                "root.energyconfigbeans",
                                "root.energyconfigurl",
                                "root.fctpricename",
                                "root.fctpricetipinfo",
                                "root.ownerdatalist",
                                "root.piccount",
                                "root.pricelist",
                                "root.salestate",
                                "root.seriesrankinfolist",
                                "root.seriesscore",
                                "root.seriesshowtype",
                                "root.showconfig",
                                "root.stopspecnum",
                                "root.supertest",
                                "root.topiconlist",
                                "root.vrinfo",
                                "root.wintertest",
                                "root.sscpricname",
                                "root.sscsaleno",
                                "root.energyconfiglist",
                                "root.videoinfo",

                                "root.edgehyperlink.bottomlist[*].pvitem.click.argvs",
                                "root.edgehyperlink.bottomlist[*].pvitem.show.argvs",

//                                "root.sscpriceinfo", //暂时排除
//                                "root.ssclinkurl",


                                "root.attentionspecid",
                                "root.attentionspecname",
                                "root.vrmaterial.jump_url", //线上的链接有点问题，多拼接了
                                "root.logo",
                                "root.edgehyperlink.bottomlist[*].url",
                                "root.vrmaterial.vrinfo_backgroudImg", //没有vr新能源默认图无效
                                "root.carparmconfig.labels",
                                "root.dealerpricerangeinfo.dealerprice", //经销商报价


                                //tabinfo过滤节点
                                "root[*].pluginversion",
                                "root[*].cardtitle",
                                "root[*].taburl"


                        ).setRootNode("result.tabinfo").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                    }
                }
            }
        }
        System.out.println("=== success =================================");
    }

    public static void testAll(List<Integer> seriesIds, String od, String nd) {
        String url = "/carbase/seriessummary/seriesbasecardinfo?cityid=%s&nodefaultcityid=%s&energytestab=test_f&yctgabtest=%s&seriesid=%s&model=1&funcabtest=%s&pm=%s&simpleinfoabtest=X&carpricelibabtest=%s&pluginversion=11.59.0";
        for (String carpricelibabtest : Arrays.asList("A", "B", "C", "D")) {
            for (String yctgabtest : Arrays.asList("A", "B")) {
                for (Integer funcabtest : Arrays.asList(0, 1)) {
                    for (Integer pm : Arrays.asList(1, 2)) {
                        for (Integer cityId : CityUtil.getAllCityIds()) {
                            for (Integer seriesId : seriesIds) {
                                String fullUrl = String.format(url, cityId, cityId, yctgabtest, seriesId, funcabtest, pm, carpricelibabtest);
                                new CompareJson().exclude(
                                        "root.dealerpriceinfo",
                                        "root.edgehyperlink.bottomlist[*].pvitem.click.argvs",
                                        "root.edgehyperlink.bottomlist[*].pvitem.show.argvs",
                                        "root.edgehyperlink.bottomlistjm",
                                        "root.energyconfigbeans",
                                        "root.energyconfigurl",
                                        "root.fctpricename",
                                        "root.fctpricetipinfo",
                                        "root.ownerdatalist",
                                        "root.piccount",
                                        "root.pricelist",
                                        "root.salestate",
                                        "root.seriesrankinfolist",
                                        "root.seriesscore",
                                        "root.seriesshowtype",
                                        "root.showconfig",
                                        "root.stopspecnum",
                                        "root.supertest",
                                        "root.topiconlist",
                                        "root.vrinfo",
                                        "root.wintertest",
                                        "root.sscpricname",
                                        "root.sscsaleno",
                                        "root.vrmaterial.jump_url",
                                        "root.logo",
                                        "root.videoinfo",
                                        "root.energyconfiglist",

                                        "root.sscpriceinfo", //暂时排除
                                        "root.edgehyperlink.bottomlist[*].url",
                                        "root.ssclinkurl",
                                        "root.vrmaterial.vrinfo_backgroudImg" //没有vr新能源默认图无效


                                ).setRootNode("result.seriesbaseinfo").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("=== success =================================");
    }
}
