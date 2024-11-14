package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.car.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/22
 */
@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class RecRankList {


    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        List<Integer> pageIndexList = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> provinceIdList = List.of(-1, 110000, 310000);
        List<String> levelIdList = List.of("1,2,3,4,5,6", "16,17,18,19,20", "21,22,23,24", "3");
        List<String> fctTypeIdList = List.of("0", "合资", "自主", "进口");
        List<Integer> energyTypeList = List.of(0, 1, 4, 5, 6, 456);
        List<String> priceList = List.of("0-9000", "10-15", "15-20", "20-25");
        List<Integer> koubeiTypeIdList = List.of(0, 3, 4, 5, 6, 7, 8, 9, 15);

        // 新旧地址
        String od = "http://cars.app.autohome.com.cn";
        String nd = "http://car-app-debug.mesh-thallo.autohome.com.cn";

//        testSscInfo(od, nd);
//        testAreaSeriesAttRankResult(pageIndexList, provinceIdList, levelIdList, fctTypeIdList, energyTypeList, priceList, od, nd);
        testAttentionNewCarListResult(pageIndexList, levelIdList, od, nd);
//        testKoubeiRankResult(pageIndexList, koubeiTypeIdList, levelIdList, energyTypeList, priceList, od, nd);
    }

    public static void testSscInfo(String od,
                                   String nd) {
        int count = 0;
        String url = "/carext/recrank/all/getrecranklistpageresult2_v2?typeid=6&channel=0&pageindex=1&price=0-9000&model=0&pagesize=20&energytype=5&pm=1&levelid=5&penetrate_version=pre21_1718104826000_1715740829000&fcttypeid=0&pluginversion=11.63.3";
        new CompareJson().exclude(
                "root.scenetitle",
                "root.list[*].followlink",
                "root.list[*].rightpricetitle",
                "root.list[*].salecount",
                "root.list[*].followcount",
                "root.list[*].followtext",
                "root.list[*].pricelinkurl"
        ).setRootNode("result").compareUrlAsync(od.concat(url), nd.concat(url)).join();
    }

    public static void testAreaSeriesAttRankResult(List<Integer> pageIndexList,
                                                   List<Integer> provinceIdList,
                                                   List<String> levelIdList,
                                                   List<String> fctTypeIdList,
                                                   List<Integer> energyTypeList,
                                                   List<String> priceList,
                                                   String od,
                                                   String nd) {
        int count = 0;
        String url = "/carext/recrank/all/getrecranklistpageresult2_v2?penetrate_version=pre26_1718104826000_1719541563000&pm=1" +
                "&pagesize=20&channel=0&subranktypeid=2001&model=1&typeid=2&pluginversion=11.64.0" +
                "&pageindex=%s" +
                "&provinceid=%s" +
                "&levelid=%s" +
                "&fcttypeid=%s" +
                "&energytype=%s" +
                "&price=%s";
        for (Integer provinceId : provinceIdList) {
            for (String levelId : levelIdList) {
                for (String fctTypeId : fctTypeIdList) {
                    for (Integer energyType : energyTypeList) {
                        for (String price : priceList) {
                            for (Integer pageIndex : pageIndexList) {
                                count++;
                                if (count % 1000 == 0) {
                                    System.out.println("当前请求数量：" + count);
                                }
                                String fullUrl = String.format(url, pageIndex, provinceId, levelId, fctTypeId, energyType, price);
                                new CompareJson().exclude(
                                        "root.list[*].followlink",
                                        "root.list[*].rightpricetitle",
                                        "root.list[*].followcount",
                                        "root.list[*].followtext"

                                ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }

    public static void testAttentionNewCarListResult(List<Integer> pageIndexList,
                                                     List<String> levelIdList,
                                                     String od,
                                                     String nd) {
        int count = 0;
        String url = "/carext/recrank/all/getrecranklistpageresult2_v2?penetrate_version=pre26_1718104826000_1719541563000&pm=1" +
                "&pagesize=20&channel=0&subranktypeid=2002&model=1&typeid=2&pluginversion=11.64.0" +
                "&pageindex=%s" +
                "&levelid=%s";
        for (String levelId : levelIdList) {
            for (Integer pageIndex : pageIndexList) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, pageIndex, levelId);
                new CompareJson().exclude(
                        "root.list[*].followlink",
                        "root.list[*].rightpricetitle",
                        "root.list[*].followcount",
                        "root.list[*].followtext"

                ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }
        }
        System.out.println("====================== success =================================");
    }

    public static void testKoubeiRankResult(List<Integer> pageIndexList,
                                            List<Integer> koubeiTypeIdList,
                                            List<String> levelIdList,
                                            List<Integer> energyTypeList,
                                            List<String> priceList,
                                            String od,
                                            String nd) {
        int count = 0;
        String url = "/carext/recrank/all/getrecranklistpageresult2_v2?penetrate_version=pre26_1718104826000_1719541563000&pm=1" +
                "&pagesize=20&channel=0&model=1&typeid=4&pluginversion=11.64.0" +
                "&pageindex=%s" +
                "&koubeitypeid=%s" +
                "&levelid=%s" +
                "&energytype=%s" +
                "&price=%s";
        for (Integer pageIndex : pageIndexList) {
            for (Integer koubeiTypeId : koubeiTypeIdList) {
                for (String levelId : levelIdList) {
                    for (Integer energyType : energyTypeList) {
                        for (String price : priceList) {
                            count++;
                            if (count % 1000 == 0) {
                                System.out.println("当前请求数量：" + count);
                            }
                            String fullUrl = String.format(url, pageIndex, koubeiTypeId, levelId, energyType, price);
                            new CompareJson().exclude(
                                    "root.list[*].followlink",
                                    "root.list[*].rightpricetitle",
                                    "root.list[*].followcount",
                                    "root.list[*].followtext"

                            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                        }
                    }
                }
            }
        }
        System.out.println("====================== success =================================");
    }


}
