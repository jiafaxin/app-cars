package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.owner.SeriesUseCarComponent;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.AreaSeriesAttentionDto;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class TabCard_SameLevel2 {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(TabCard_PlayCar.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        SeriesUseCarComponent service = ac.getBean(SeriesUseCarComponent.class);

        String od = "http://localhost:8188";
        String nd = "http://localhost:8881";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();

//        List<Integer> tempList = new ArrayList<>() {{
//            add(729);
//        }};
//
//        tempList.forEach(seriesId -> {
//            service.refreshOne(x -> {
//            }, seriesId, 110100);
//
//            ThreadUtil.sleep(1000);
//            test(tempList, od, nd);
//        });
//
//        test(seriesIds, od, nd);


        AreaSeriesAttentionComponent bean1 = ac.getBean(AreaSeriesAttentionComponent.class);
        List<AreaSeriesAttentionDto> objectList = bean1.get(-1);
        List<AreaSeriesAttentionDto> list = objectList.stream().filter(p -> p.getIsNewEnergy() == 1).toList();

        System.out.println(list.size());
    }


    public static void test(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carstreaming/seriessummary/tabcard?pm=2&pluginversion=11.65.2&tabid=12&tabids=12&seriesid=%s&cityid=110100&model=1&deviceid=4cbed6e8_617c_4894_a1d1_62418ae9732c";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String fullUrl = String.format(url, seriesId);
            new CompareJson().exclude(
                    ""
            ).setRootNode("result.list").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
        }

        System.out.println("=== success =================================");
    }
}
