package com.autohome.car;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.owner.SeriesUseCarComponent;
import com.autohome.car.tools.CompareJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class TabCard_HotComment {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    public static void main(String[] args) {


        ApplicationContext ac = SpringApplication.run(TabCard_PlayCar.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        SeriesUseCarComponent service = ac.getBean(SeriesUseCarComponent.class);

        String od = "http://localhost:8188";
        String nd = "http://localhost:8881";
        List<Integer> seriesIds = bean.getAllSeriesIds().stream().sorted().toList();

        List<Integer> tempList = new ArrayList<>() {{
            add(7491);
            add(634);
            add(3430);
            add(46);
            add(771);
            add(6950);
            add(4658);
            add(3411);
            add(4174);
            add(6337);
        }};

//        tempList.forEach(seriesId -> {
//            service.refreshOne(x -> {
//            }, seriesId, 110100);
//
//            ThreadUtil.sleep(1000);
//            test(tempList, od, nd);
//        });

        test(tempList, od, nd);

//        test(seriesIds, od, nd);
    }

    public static void test(List<Integer> seriesIds, String od, String nd) {
        int count = 0;
        String url = "/carstreaming/seriessummary/tabcard?pm=2&pluginversion=11.65.2&tabid=21&tabids=21&seriesid=%s&cityid=110100&model=1&deviceid=4cbed6e8_617c_4894_a1d1_62418ae9732c&comtabtextab=B&hotcommenttextab=B&aiviewpointab=A";
        for (Integer seriesId : seriesIds) {
            count++;
            if (count % 1000 == 0) {
                System.out.println("当前请求数量：" + count);
            }
            String fullUrl = String.format(url, seriesId);
            new CompareJson().exclude(
                    "root.righticonlist[*]",
                    "root.buttonbtnlist[*].animateiconlist[*]",
                    "root.bottombtn",
                    "root.toprightbtn.text",
                    "root.buttonbtnlist[1].text"
            ).setRootNode("result").compareUrlAsync(od.concat(fullUrl), nd.concat(fullUrl)).join();
        }

        System.out.println("=== success =================================");
    }
}
