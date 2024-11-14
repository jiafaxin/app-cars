package com.autohome.car;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.car.tools.CompareJson;
import com.autohome.car.tools.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/3/22
 */
@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class Speccarsbaseinfo {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SpecMapper specMapper = ac.getBean(SpecMapper.class);

        // 新旧地址
        String od = "http://cars.app.autohome.com.cn";
//        String nd = "http://localhost:8881";
//        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        String nd = "http://car.app.autohome.com.cn";

        // 车型id
        List<Integer> specIds = specMapper.getAllSpecIds();
//        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
//        specIds.addAll(cvSpecIds);
        specIds = specIds.stream().distinct().sorted(Comparator.reverseOrder()).toList();

        // 城市 （北京+澳门+喀什+西安+重庆+上海+海口）
        List<Integer> cityIds = Arrays.asList(110100);
//        List<Integer> cityIds = Arrays.asList(110100, 820100, 653100, 610100, 500100, 310100, 460100);
//        List<Integer> cityIds = CityUtil.getAllCityIds().stream().sorted().collect(Collectors.toList());

        // AB实验值
//        List<String> carPriceLibAbTests = Arrays.asList("A", "B");
        List<String> carPriceLibAbTests = Arrays.asList("A");
//        List<String> funcAbTests = Arrays.asList("A", "B");
//        List<String> funcAbTests = Arrays.asList("1","0");
        List<String> funcAbTests = Arrays.asList("1");
//        List<String> zixunAbTests = Arrays.asList("A", "B");
        List<String> zixunAbTests = Arrays.asList("B");

        // 验证方法
//        testSpecsAndCitys(specIds, cityIds, carPriceLibAbTests, funcAbTests, zixunAbTests, od, nd);
//        testPriceInfoOfSpecsAndCitys(specIds, cityIds, carPriceLibAbTests, funcAbTests, zixunAbTests);
        testZixunInfoOfSpecsAndCitys(specIds, cityIds, carPriceLibAbTests, funcAbTests, zixunAbTests);
//        testNoInfo(specIds, nd);
//        testSscInfo(specIds,110100,funcAbTests,od,nd);
    }

    public static void testNoInfo(List<Integer> specIds, String nd) {
        int count = 0;
        String url = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.60.0&carpricelibabtest=%s";
        for (Integer specId : specIds) {
            for (Integer cityId : List.of(110100)) {
                for (String carPriceLibAbTest : Arrays.asList("A", "B")) {
                    count++;
                    if (count % 1000 == 0) {
                        System.out.println("当前请求数量：" + count);
                    }
                    String fullUrl = String.format(url, specId, cityId, carPriceLibAbTest);

                    try {

                        CompletableFuture<String> future = HttpClient.getString(nd.concat(fullUrl), "UTF-8", "");
                        String result = future.join();
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("carparmconfig"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-carparmconfig-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("dealermodules"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-dealermodules-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("practicalinfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-practicalinfo-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("pricelist"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-pricelist-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("specbaseinfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-specbaseinfo-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("specpicinfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-specpicinfo-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("tabinfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-tabinfo-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("priceinfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-priceinfo-" + nd.concat(fullUrl));
                        }
                        if (Objects.isNull(JSONObject.parseObject(jsonObject.get("result").toString()).get("zixuninfo"))) {
                            System.out.println(DateUtil.format(new Date(), "HH:mm:ss") + "-zixuninfo-" + nd.concat(fullUrl));
                        }
                    } catch (Exception e) {
                        System.out.println("车型" + specId + "异常:" + e.toString());
                    }
                }
            }
        }
        System.out.println("==================== success ============================");
    }


    public static void testSscInfo(List<Integer> specIds, int cityid, List<String> funcAbTests, String od,
                                   String nd) {
        int count = 0;
        String url = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.61.5&carpricelibabtest=A&funcabtest=%s&zixunabtest=B";

        for (Integer specId : specIds) {
            for (String funcAb : funcAbTests) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, specId, "110100",funcAb);
                new CompareJson().exclude(
                        "root.caculateinfo",
                        "root.configinfo",
                        "root.difconfiginfo",
                        "root.newpublicaskprice",
                        "root.newpublicaskpricea",
                        "root.newpublicaskpriceb",
                        "root.newpublicaskpricec",
                        "root.publicaskprice",
                        "root.tabinfos",
                        "root.taxhalfinfo",
                        "root.tabinfosb",
                        "root.tabinfosnew",
                        "root.specpicinfo.pvitem.show.argvs",
                        "root.specpicinfo.pvitem.click.argvs",
                        "root.specpicinfo.difpiclist",
                        "root.specpicinfo.diftip",
                        "root.specpicinfo.diftiplinkurl",
                        "root.specpicinfo.diftippvitem",
                        "root.specpicinfo.tip",
                        "root.specpicinfo.piclist[*].pvitem.show.argvs",
                        "root.specpicinfo.piclist[*].pvitem.click.argvs",
                        "root.specbaseinfo.intminprice",
                        "root.pricelist[*].pvitem.show.argvs",
                        "root.pricelist[*].pvitem.click.argvs",
                        "root.practicalinfo.pvitem.show.argvs",
                        "root.practicalinfo.pvitem.click.argvs",
                        "root.zixuninfo",
                        // 下列为图片地址差异、pv差异，暂时屏蔽便于观察
                        "root.specbaseinfo.logo", // 带括号的问题
                        "root.specbaseinfo.brandlogo", // 带括号的问题
                        "root.specbaseinfo.energetypename",
                        "root.practicalinfo.pvitem",
                        "root.specpicinfo.linkurl", // 带括号的问题
                        "root.specpicinfo.piclist[*]", // 带括号的问题
                        "root.specbaseinfo.energetype", // 大多原因是未售车型，不外显相关参数
                        "root.pricelist[0].subtitle", // 计算器价格，应该没什么问题，不一致看起来是源接口缓存问题
                        "root.pricelist[0].linkurl", // 或者app-cars的车型+城市的经销商价格缓存问题
                        "root.pricelist[1].subtitle",
                        "root.pricelist[1].linkurl",
                        "root.pricelist[3]"

                ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }
        }
    }

    public static void testSpecsAndCitys(List<Integer> specIds,
                                         List<Integer> cityIds,
                                         List<String> carPriceLibAbTests,
                                         List<String> funcAbTests,
                                         List<String> zixunAbTests,
                                         String od,
                                         String nd) {
        int count = 0;
        String url = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.61.5&carpricelibabtest=%s&funcabtest=%s&zixunabtest=%s";
        for (Integer specId : specIds) {
            for (Integer cityId : cityIds) {
                for (String carPriceLibAbTest : carPriceLibAbTests) {
                    for (String funcAbTest : funcAbTests) {
                        for (String zixunAbTest : zixunAbTests) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl = String.format(url, specId, cityId, carPriceLibAbTest, funcAbTest, zixunAbTest);
                            new CompareJson().exclude(
                                    "root.caculateinfo",
                                    "root.configinfo",
                                    "root.difconfiginfo",
                                    "root.newpublicaskprice",
                                    "root.newpublicaskpricea",
                                    "root.newpublicaskpriceb",
                                    "root.newpublicaskpricec",
                                    "root.publicaskprice",
                                    "root.tabinfos",
                                    "root.taxhalfinfo",
                                    "root.tabinfosb",
                                    "root.tabinfosnew",
                                    "root.specpicinfo.pvitem.show.argvs",
                                    "root.specpicinfo.pvitem.click.argvs",
                                    "root.specpicinfo.difpiclist",
                                    "root.specpicinfo.diftip",
                                    "root.specpicinfo.diftiplinkurl",
                                    "root.specpicinfo.diftippvitem",
                                    "root.specpicinfo.tip",
                                    "root.specpicinfo.piclist[*].pvitem.show.argvs",
                                    "root.specpicinfo.piclist[*].pvitem.click.argvs",
                                    "root.specbaseinfo.intminprice",
                                    "root.pricelist[*].pvitem.show.argvs",
                                    "root.pricelist[*].pvitem.click.argvs",
                                    "root.practicalinfo.pvitem.show.argvs",
                                    "root.practicalinfo.pvitem.click.argvs",
                                    "root.zixuninfo",
                                    // 下列为图片地址差异、pv差异，暂时屏蔽便于观察
                                    "root.specbaseinfo.logo", // 带括号的问题
                                    "root.specbaseinfo.brandlogo", // 带括号的问题
                                    "root.practicalinfo.pvitem",
                                    "root.specpicinfo.linkurl", // 带括号的问题
                                    "root.specpicinfo.piclist[*]", // 带括号的问题
                                    "root.specbaseinfo.energetype", // 大多原因是未售车型，不外显相关参数
                                    "root.specbaseinfo.sscpriceinfo",
                                    "root.specbaseinfo.sscpricname",
                                    "root.specbaseinfo.tabdefaluttypeid",
                                    "root.pricelist[0].subtitle", // 计算器价格，应该没什么问题，不一致看起来是源接口缓存问题
                                    "root.pricelist[0].linkurl", // 或者app-cars的车型+城市的经销商价格缓存问题
                                    "root.pricelist[1].subtitle",
                                    "root.pricelist[1].linkurl",
                                    "root.pricelist[3]"

                            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }


    public static void testZixunInfoOfSpecsAndCitys(List<Integer> specIds,
                                                    List<Integer> cityIds,
                                                    List<String> carPriceLibAbTests,
                                                    List<String> funcAbTests,
                                                    List<String> zixunAbTests) {
        String od = "http://car.app.autohome.com.cn";
        String nd = "http://cars.app.autohome.com.cn";
        int count = 0;
        String url1 = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.61.5&carpricelibabtest=%s&funcabtest=%s&zixunabtest=%s";
        String url2 = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.61.5&carpricelibabtest=%s&funcabtest=%s&zixunabtest=%s";
        specIds = specIds.stream().filter(e -> e < 66000).collect(Collectors.toList());
        for (Integer specId : specIds) {
            for (Integer cityId : cityIds) {
                for (String carPriceLibAbTest : carPriceLibAbTests) {
                    for (String funcAbTest : funcAbTests) {
                        for (String zixunAbTest : zixunAbTests) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl1 = String.format(url1, specId, cityId, carPriceLibAbTest, funcAbTest, zixunAbTest);
                            String fullUrl2 = String.format(url1, specId, cityId, carPriceLibAbTest, funcAbTest, zixunAbTest);

                            String a = HttpClient.getString(od.concat(fullUrl1), "UTF-8", "").join();
                            String b = HttpClient.getString(nd.concat(fullUrl2), "UTF-8", "").join();
                            Object aResult = JSONObject.parseObject(a).get("result");
                            Object aZixuninfo =
                                    JSONObject.parseObject(aResult.toString()).get("zixuninfo");
                            Object aDatalist =
                                    JSONObject.parseObject(aZixuninfo.toString()).get("datalist");
                            List<Map> aDataListMap =
                                    JSONArray.parseArray(aDatalist.toString(), Map.class);
                            aDataListMap = aDataListMap.stream().sorted(Comparator.comparing(Map::hashCode))
                                    .toList();
                            Object aBgurl =
                                    JSONObject.parseObject(aZixuninfo.toString()).get("bgurl");
                            Object aInterval =
                                    JSONObject.parseObject(aZixuninfo.toString()).get("interval");

                            Object bResult =
                                    JSONObject.parseObject(b).get("result");
                            Object bZixuninfo =
                                    JSONObject.parseObject(bResult.toString()).get("zixuninfo");
                            Object bDatalist =
                                    JSONObject.parseObject(bZixuninfo.toString()).get("datalist");
                            List<Map> bDataListMap = JSONArray.parseArray(bDatalist.toString(), Map.class);
                            bDataListMap = bDataListMap.stream().sorted(Comparator.comparing(Map::hashCode))
                                    .toList();
                            Object bBgurl =
                                    JSONObject.parseObject(aZixuninfo.toString()).get("bgurl");
                            Object bInterval =
                                    JSONObject.parseObject(aZixuninfo.toString()).get("interval");

                            if (!StringUtils.equals(aBgurl.toString(), bBgurl.toString())) {
                                System.out.println("A:" + od.concat(fullUrl1));
                                System.out.println("B:" + nd.concat(fullUrl2));
                            }
                            if (!StringUtils.equals(aInterval.toString(), bInterval.toString())) {
                                System.out.println("A:" + od.concat(fullUrl1));
                                System.out.println("B:" + nd.concat(fullUrl2));
                            }
                            if (!StringUtils.equals(aDataListMap.toString(),
                                    bDataListMap.toString())) {
                                System.out.println("A:" + od.concat(fullUrl1));
                                System.out.println("B:" + nd.concat(fullUrl2));
                            }
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }

    public static void testPriceInfoOfSpecsAndCitys(List<Integer> specIds,
                                                    List<Integer> cityIds,
                                                    List<String> carPriceLibAbTests,
                                                    List<String> funcAbTests,
                                                    List<String> zixunAbTests) {
        String od = "http://car.app.autohome.com.cn";
        String nd = "https://cars.app.autohome.com.cn";
        int count = 0;
        String url1 = "/carbase/specsummary/carsbaseinfo?pm=1&specid=%s&cityid=%s&model=1&pluginversion=11.61.5&carpricelibabtest=%s&funcabtest=%s&zixunabtest=%s";
        String url2 = "/carext/specsummary/v10/carsderivative?pm=1&specid=%s&cityid=%s&nodefaultcityid=%s&model=1&pluginversion=11.61.5";
        specIds = specIds.stream().filter(e -> e < 66000).collect(Collectors.toList());
        for (Integer specId : specIds) {
            for (Integer cityId : cityIds) {
                for (String carPriceLibAbTest : carPriceLibAbTests) {
                    for (String funcAbTest : funcAbTests) {
                        for (String zixunAbTest : zixunAbTests) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl1 = String.format(url1, specId, cityId, carPriceLibAbTest, funcAbTest, zixunAbTest);
                            String fullUrl2 = String.format(url2, specId, cityId, cityId);
                            new CompareJson().exclude(
                                    "root.carparmconfig",
                                    "root.dealermodules",
                                    "root.practicalinfo",
                                    "root.pricelist",
                                    "root.specbaseinfo",
                                    "root.specpicinfo",
                                    "root.tabinfo",
                                    "root.zixuninfo"
                            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl1), nd.concat(fullUrl2)).join();
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }

}
