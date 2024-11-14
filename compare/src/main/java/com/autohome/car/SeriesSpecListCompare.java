package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.car.tools.CompareJson;
import com.autohome.car.tools.HttpClient;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author chengjincheng
 * @date 2024/6/5
 */
@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class SeriesSpecListCompare {

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper seriesMapper = ac.getBean(SeriesMapper.class);

        // 新旧地址
        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://car-app-debug.mesh-thallo.autohome.com.cn";
//        String nd = "http://car.app.autohome.com.cn";

        // 车型id
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        seriesIds = seriesIds.stream().distinct().sorted(Comparator.reverseOrder()).toList();
//        seriesIds = seriesIds.stream().distinct().toList();

        // 城市 （北京+澳门+喀什+西安+重庆+上海+海口）
        List<Integer> cityIds = Arrays.asList(110100);
//        List<Integer> cityIds = Arrays.asList(110100, 820100, 653100, 610100, 500100, 310100, 460100);
//        List<Integer> cityIds = CityUtil.getAllCityIds().stream().sorted().collect(Collectors.toList());

        // AB实验值
//        List<String> zixunAbTests = Arrays.asList("A", "B");
        List<String> zixunAbTests = Arrays.asList("B");

        // 验证方法
//        testSpecsAndCitys(seriesIds, cityIds, zixunAbTests, od, nd);
        testCjc(seriesIds, cityIds, zixunAbTests, od, nd);
//        allSeriesCompare(seriesIds,cityIds,od,nd);
    }

    /**
     * 全车系对比，默认Tab内容:车型分组对比+++分组下的车型列表对比
     */
    public static void allSeriesCompare(List<Integer> seriesIds,
                                        List<Integer> cityIds,
                                        String od,
                                        String nd) {
        int count = 0;
        String url = "/carbase/seriessummary/specbaselist?seriesid=%s&cityid=%s&tagid=%s&tagname=%s&zixunabtest=%s&model=1&pm=1&pluginversion=11.63.0";
        for (Integer seriesId : seriesIds) {
            for (Integer cityId : cityIds) {
                String tag1Url = String.format(url, seriesId, cityId, 1, "", "");

                /**
                 * 对下二级tab条件
                 */
                String result = HttpClient.getString(od + tag1Url, "UTF-8", "").join();
                yearData yearData = JsonUtil.toObject(result, yearData.class);
                if (Objects.nonNull(yearData)
                        && Objects.nonNull(yearData.getResult())
                        && Objects.nonNull(yearData.getResult().getSpecinfo())
                        && !CollectionUtils.isEmpty(yearData.getResult().getSpecinfo().getYearList())) {
                    List<SeriesSpecListCompare.yearData.YearList> yearListList =
                            yearData.getResult().getSpecinfo().getYearList();
                    for (SeriesSpecListCompare.yearData.YearList yearList : yearListList) {
                        count++;
                        tag1Url = String.format(url, seriesId, cityId, yearList.getYearvalue(), yearList.yearname, "");
                        Compare(od.concat(tag1Url), nd.concat(tag1Url), count);
                    }
                }

                /**
                 * 只对比默认tab
                 */
//                count++;
//                Compare(od.concat(tag1Url), nd.concat(tag1Url),count);
            }
        }
        System.out.println("====================== success =================================");
    }

    static void Compare(String OldUrl, String NewUrl, int count) {
        if (count % 100 == 0) {
            System.out.println("当前请求数量：" + count);
        }
        new CompareJson().exclude(
                "root.salecarinfo",
                "root.seriesbaseinfo",
                "root.tabinfosb",
                "root.specbottomlist",
                "root.specinfo.dynamicinfo",
                "root.specinfo.orderlist",
                "root.specinfo.seriesaskpriceinfo",
                "root.specinfo.staticspeclist",
                "root.specinfo.yearlist[*].yearvalue",
                "root.specinfo.speclist[*].yearvalue",
                "root.specinfo.speclist[*].nodealertip",
                "root.specinfo.speclist[*].yearspeclist[*].nodealertip",
                //"root.specinfo.speclist[*].yearspeclist[*].yearname",//
                //root.specinfo.speclist[*].yearspeclist[*].speclist[*]  下的全字段
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].apolegamy",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].attention",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].attentioninfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].attentionint",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].calcprice",//经销商价会覆盖这个
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].canaskprice",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].carmailurl",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].configinfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].description",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].diffconfiginfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].diffconfiginfo.configlist",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].downprice",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].electriccarname",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].electriccarval",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].endurancemileage",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].endurancetext",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].ext",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].fctpricetipinfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypedetail",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypedetailid",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypeid",//这个类型不用了
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].icon",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].id",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].iminfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].imtype",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].isother",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].istaxexemption",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].labletype",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].mali",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].minprice",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].minpricename",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].name",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].noshowprice",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].order",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pailiang",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].paramisshow",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].price",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricedownlabel",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricename",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricetip",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].recinfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].recommendcar",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].roomid",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].salebtn",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].saletype",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].seatcount",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].specbottomtitle",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].specbottomurl",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscllist",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscprice",
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].state",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].telinfo",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscpricename",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].usedcarpricebtn",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].ssclinkurl",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscpvitem",
                "root.specinfo.speclist[*].yearspeclist[*].speclist[*].showpricealerttip"
                //"root.specinfo.speclist[*].yearspeclist[*].speclist[*].year"
        ).excludeResult("{}").setRootNode("result").compareUrlAsync(OldUrl, NewUrl).join();
    }

    public static void testSpecsAndCitys(List<Integer> seriesIds,
                                         List<Integer> cityIds,
                                         List<String> zixunAbTests,
                                         String od,
                                         String nd) {
        int count = 0;
        String url = "/carbase/seriessummary/specbaselist?seriesid=%s&cityid=%s&tagid=%s&tagname=%s&zixunabtest=%s&model=1&pm=1&pluginversion=11.63.0";
        for (Integer seriesId : seriesIds) {
            for (Integer cityId : cityIds) {
                for (String zixunAbTest : zixunAbTests) {
                    String tag1Url = String.format(url, seriesId, cityId, 1, null, zixunAbTest);
                    String result = HttpClient.getString(od + tag1Url, "UTF-8", "").join();
                    yearData yearData = JsonUtil.toObject(result, yearData.class);
                    if (Objects.nonNull(yearData)
                            && Objects.nonNull(yearData.getResult())
                            && Objects.nonNull(yearData.getResult().getSpecinfo())
                            && !CollectionUtils.isEmpty(yearData.getResult().getSpecinfo().getYearList())) {
                        List<SeriesSpecListCompare.yearData.YearList> yearListList =
                                yearData.getResult().getSpecinfo().getYearList();
                        for (SeriesSpecListCompare.yearData.YearList yearList : yearListList) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl = String.format(url, seriesId, cityId,
                                    yearList.getYearvalue(), yearList.getYearname(), zixunAbTest);
                            new CompareJson().exclude(
                                    "root.salecarinfo",
                                    "root.seriesbaseinfo",
                                    "root.tabinfosb",
                                    "root.specinfo.dynamicinfo",
                                    "root.specinfo.seriesaskpriceinfo",
                                    "root.specinfo.staticspeclist"

                            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }


    public static void testCjc(List<Integer> seriesIds,
                               List<Integer> cityIds,
                               List<String> zixunAbTests,
                               String od,
                               String nd) {
        int count = 0;
        String url = "/carbase/seriessummary/specbaselist?seriesid=%s&cityid=%s&tagid=%s&tagname=%s&zixunabtest=%s&model=1&pm=1&pluginversion=11.63.0";
        for (Integer seriesId : seriesIds) {
            for (Integer cityId : cityIds) {
                for (String zixunAbTest : zixunAbTests) {
                    String tag1Url = String.format(url, seriesId, cityId, 1, null, zixunAbTest);
                    String result = HttpClient.getString(od + tag1Url, "UTF-8", "").join();
                    yearData yearData = JsonUtil.toObject(result, yearData.class);
                    if (Objects.nonNull(yearData)
                            && Objects.nonNull(yearData.getResult())
                            && Objects.nonNull(yearData.getResult().getSpecinfo())
                            && !CollectionUtils.isEmpty(yearData.getResult().getSpecinfo().getYearList())) {
                        List<SeriesSpecListCompare.yearData.YearList> yearListList =
                                yearData.getResult().getSpecinfo().getYearList();
                        for (SeriesSpecListCompare.yearData.YearList yearList : yearListList) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl = String.format(url, seriesId, cityId,
                                    yearList.getYearvalue(), yearList.getYearname(), zixunAbTest);
                            new CompareJson().exclude(
                                    // im调接口直接获取
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].iminfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].imtype",
                                    // 个别不一致
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].isother",
                                    // 不再使用
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].apolegamy",
                                    // 已验证（由于更换接口，直接取经销商新接口中的按钮及文案，文案会出现和线上不一致情况）
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].specbottomurl",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].specbottomtitle",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].canaskprice",
                                    // 已验证
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].zixuninfo",
                                    // ========================================================================
                                    // 已验证
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].downprice",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].calcprice",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].minprice",
                                    // ========================================================================
                                    // 已验证
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscprice",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].sscpricename",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].usedcarpricebtn",
                                    // ========================================================================
                                    "root.salecarinfo",
                                    "root.specbottomlist",
                                    "root.seriesbaseinfo",
                                    "root.tabinfosb",
                                    "root.specinfo.dynamicinfo",
                                    "root.specinfo.seriesaskpriceinfo",
                                    "root.specinfo.staticspeclist",
                                    "root.specinfo.speclist[*].yearname",
                                    "root.specinfo.yearlist[*].yearvalue",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].telinfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].configinfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].carmailurl",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].description",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].icon",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].diffconfiginfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].endurancemileage",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].endurancetext",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].ext",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fctpricetipinfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypedetail",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypedetailid",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].fueltypeid",
//                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].id",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].istaxexemption",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].labletype",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].mali",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].name",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].noshowprice",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].order",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pailiang",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].paramisshow",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].price",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricedownlabel",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricename",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].pricetip",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].recinfo",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].recommendcar",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].salebtn",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].saletype",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].seatcount",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].state",
                                    "root.specinfo.speclist[*].yearspeclist[*].speclist[*].year"

                            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }


    @Data
    private static class yearData {
        private Result result;

        @Data
        private static class Result {
            private SpecInfo specinfo;

        }

        @Data
        private static class SpecInfo {
            private List<YearList> yearList;
        }

        @Data
        private static class YearList {
            private String yearname;
            private int yearvalue;
        }
    }
}
