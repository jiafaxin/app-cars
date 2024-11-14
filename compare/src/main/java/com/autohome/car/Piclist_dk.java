package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecBaseInfoEntity;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class Piclist_dk {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesSpecComponent seriesSpecComponent;

    public static void main(String[] args) {

        ApplicationContext ac = SpringApplication.run(Seriesbasecardinfo.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        SpecMapper specBean = ac.getBean(SpecMapper.class);

        String od = "http://cars.app.autohome.com.cn";
//        String nd = "http://car-app.mesh-thallo.autohome.com.cn";
        String nd = "http://car-app2.mesh-thallo.autohome.com.cn";
//        String nd = "http://localhost:8881";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();
        List<SpecBaseInfoEntity> allSpecBaseInfo = specBean.getAllSpecBaseInfo();


//        testUrl();
//        testspecvrmaterial(seriesIds, od, nd, allSpecBaseInfo);
        testvrmaterial(seriesIds, od, nd);
//        testh5vr(seriesIds, od, nd, allSpecBaseInfo);
//        test7008(seriesIds, od, nd);
//        test1111(seriesIds, od, nd);

        System.out.println("=== success =================================");


    }

    public static void testUrl() {
        new CompareJson().exclude(

                "root.vrmaterial.jump_url", //线上拼接错误

                //线上默认值字段
                "root.refitinfo",
                "root.tdcarinfo",
                "root.cartest",

                "root.picgrouplist",
                "root.piclist",
                "root.colorlist",

                "root.pagecount",

                //无效字段
                "root.voiceicon",
                "root.voicetitle",
                "root.arinfo",
                "root.showownerpicspecid",
                "root.showownerpicposition",
                "root.rowcount",
                "root.color_list",
//                            "root.edgehyperlink.bottomlist[*].pvitem.click.argvs",
                "root.piclist[*].locationid",
                "root.piclist[*].membername",
                "root.piclist[*].displacement",
                "root.vr.pvitem.show.argvs",
                "root.vr.pvitem.click.argvs",

                //tabinfo过滤节点
//                            "root[*].pluginversion",
                ""


        ).setRootNode("result").compareUrlAsync(
                "http://cars.app.autohome.com.cn/carbase/pic/getpiclist?pm=1&categoryid=1&isinner=1&seriesid=146&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C",
                "http://car-app2.mesh-thallo.autohome.com.cn/carbase/pic/getpiclist?pm=1&categoryid=1&isinner=1&seriesid=146&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C").join();


    }

    public static void testspecvrmaterial(List<Integer> seriesIds, String od, String nd, List<SpecBaseInfoEntity> allSpecBaseInfo) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=1&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&videovrabtest=C";
        for (String categoryid : Arrays.asList("1", "10")) {
            for (Integer seriesId : seriesIds) {

                //过来车系下车型
                List<SpecBaseInfoEntity> specBaseInfoEntities = allSpecBaseInfo.stream().filter(e -> e.getSeriesId() == seriesId).toList();
                if (specBaseInfoEntities.isEmpty()) {
                    continue;
                }

                for (SpecBaseInfoEntity specBaseInfoEntity : specBaseInfoEntities) {
                    count++;
                    if (count % 1000 == 0) {
                        System.out.println("当前请求数量：" + count);
                    }
                    String fullUrl = String.format(url + "&specid=" + specBaseInfoEntity.getId(), categoryid, seriesId);
                    new CompareJson().exclude(

                            "root.vrmaterial.jump_url", //线上拼接错误
                            "root.vrmaterial.salestatetip",
                            "root.vrmaterial.salestate",
                            "root.vrmaterial.is3dpk",
                            "root.vrmaterial.specname",


                            //线上默认值字段
                            ""


                    ).setRootNode("root.vrmaterial").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                }
            }

        }
        System.out.println("=== success =================================");
    }

    public static void testvrmaterial(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=1&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&videovrabtest=C&specid=0";
        for (String categoryid : Arrays.asList("1", "10")) {
            for (Integer seriesId : seriesIds) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, categoryid, seriesId);
                new CompareJson().exclude(

                        "root.jump_url", //线上拼接错误

                        //线上默认值字段
                        "root.refitinfo",
                        "root.tdcarinfo",
                        "root.cartest",

                        "root.picgrouplist",
                        "root.piclist",
                        "root.colorlist",

                        "root.pagecount",

                        //无效字段
                        "root.voiceicon",
                        "root.voicetitle",
                        "root.arinfo",
                        "root.showownerpicspecid",
                        "root.showownerpicposition",
                        "root.rowcount",
                        "root.color_list",
                        "root.piclist[*].locationid",
                        "root.piclist[*].membername",
                        "root.piclist[*].displacement",

                        "root.vr.pvitem.show.argvs",
                        "root.vr.pvitem.click.argvs",
                        "root.actionvideoinfo.pvitem.show.argvs",
                        "root.actionvideoinfo.pvitem.click.argvs",


                        ""


                ).setRootNode("result.vrmaterial").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }

        }
        System.out.println("=== success =================================");
    }


    public static void testh5vr(List<Integer> seriesIds, String od, String nd, List<SpecBaseInfoEntity> allSpecBaseInfo) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=1&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&videovrabtest=C";
        for (String categoryid : Arrays.asList("10")) {
            for (Integer seriesId : seriesIds) {
                //过来车系下车型
                List<SpecBaseInfoEntity> specBaseInfoEntities = allSpecBaseInfo.stream().filter(e -> e.getSeriesId() == seriesId).toList();
                if (specBaseInfoEntities.isEmpty()) {
                    continue;
                }

                for (SpecBaseInfoEntity specBaseInfoEntity : specBaseInfoEntities) {
                    count++;
                    if (count % 1000 == 0) {
                        System.out.println("当前请求数量：" + count);
                    }
                    String fullUrl = String.format(url + "&specid=" + specBaseInfoEntity.getId(), categoryid, seriesId);
                    new CompareJson().exclude(
                            ""
                    ).setRootNode("result.h5vrinfo").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
                }


            }

        }
        System.out.println("=== success =================================");
    }

    /**
     * 改装图 比较
     * result.piclist
     * root.vrmaterial
     *
     * @param seriesIds
     * @param od
     * @param nd
     */
    public static void test1111(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=1&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C";
        for (String categoryid : Arrays.asList("1111")) {
            for (Integer seriesId : seriesIds) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, categoryid, seriesId);
                new CompareJson().exclude(
                        "root.actionvideoinfo",
                        "root.h5vrinfo",
                        "root.vr",


                        //线上默认值字段
                        "root.refitinfo",
                        "root.tdcarinfo",
                        "root.cartest",

//                        "root.picgrouplist",
//                        "root.colorlist",


                        //无效字段
                        "root.voiceicon",
                        "root.voicetitle",
                        "root.arinfo",
                        "root.showownerpicspecid",
                        "root.showownerpicposition",
                        "root.pageindex",
                        "root.pagecount",
                        "root.rowcount",
                        "root.piclist[*].locationid",
                        "root.piclist[*].membername",
                        "root.piclist[*].displacement",
                        ""
                ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }

        }
        System.out.println("=== success =================================");
    }

    /**
     * 7008 概览图 比较
     * result.gltablist
     * result.piclist
     *
     * @param seriesIds
     * @param od
     * @param nd
     */
    public static void test7008(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carbase/pic/getpiclist?pm=1&categoryid=%s&isinner=1&seriesid=%s&pagesize=60&colorid=0&pageindex=1&reqpicgroup=0&sizelevel=l2&pluginversion=11.61.5&specid=0&videovrabtest=C";
        for (String categoryid : Arrays.asList("7008")) {
            for (Integer seriesId : seriesIds) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println("当前请求数量：" + count);
                }
                String fullUrl = String.format(url, categoryid, seriesId);
                new CompareJson().exclude(

                        "root.vrmaterial",
                        "root.actionvideoinfo",
                        "root.h5vrinfo",
                        "root.vr",


                        //线上默认值字段
                        "root.refitinfo",
                        "root.tdcarinfo",
                        "root.cartest",

                        "root.picgrouplist",
                        "root.colorlist",


                        //无效字段
                        "root.voiceicon",
                        "root.voicetitle",
                        "root.arinfo",
                        "root.showownerpicspecid",
                        "root.showownerpicposition",
                        "root.pageindex",
                        "root.pagecount",
                        "root.rowcount",
                        "root.color_list",
                        "root.piclist[*].locationid",
                        "root.piclist[*].membername",
                        "root.piclist[*].displacement",
                        ""
                ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
            }

        }
        System.out.println("=== success =================================");
    }
}
