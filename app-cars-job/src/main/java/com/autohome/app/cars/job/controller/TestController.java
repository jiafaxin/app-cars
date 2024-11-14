package com.autohome.app.cars.job.controller;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.StringUtils;
import com.autohome.app.cars.job.kafka_listeners.BaseCarListener;
import com.autohome.app.cars.mapper.car.HqPicMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.HqPhotoComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecYearNewComponent;
import com.autohome.app.cars.service.components.car.dtos.HqPhotoDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.che168.SeriesYearCityPriceComponent;
import com.autohome.app.cars.service.components.che168.SpecUsedCarPriceComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.hangqing.CitySortHangqingComponent;
import com.autohome.app.cars.service.components.hangqing.SpecCityPriceHisComponent;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHisDto;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.app.cars.service.components.hqpic.HqPicDataComponent;
import com.autohome.app.cars.service.services.dtos.MegaDataDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import javax.faces.flow.builder.ReturnBuilder;
import java.util.*;

/**
 * @author chengjincheng
 * @date 2024/4/18
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private BaseCarListener baseCarListener;

    @Autowired(required = false)
    private Map<String, IJobHandler> jobMap;


    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping(value = "/kafka/listen/test", produces = "application/json;charset=utf-8")
    public String kafkaListenTest() {
        String kafkaMessage = "{\"action\":\"update\",\"business\":\"car\",\"data\":{\"specId\":65293},\"type\":\"spec\"}";
        //baseCarListener.listen(kafkaMessage);
        return "success";
    }

    @GetMapping(value = "/kafka/send/test", produces = "application/json;charset=utf-8")
    public String kafkaSendTest(String kafkaMessage) {
        if (StringUtils.isEmpty(kafkaMessage)) {
            kafkaMessage = "{\"action\":\"update\",\"business\":\"car\",\"data\":{\"specId\":65293},\"type\":\"spec\"}";
        }
        kafkaTemplate.send("autohome_data_transit", kafkaMessage);
        return "success";
    }

    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @GetMapping(value = "/series/new", produces = "application/json;charset=utf-8")
    public String seriesTest() {
        List<Integer> seriesIdList = seriesMapper.getAllSeriesIds().stream().sorted(Comparator.reverseOrder()).toList();
        seriesIdList = seriesIdList.subList(0, 500);
        List<SeriesDetailDto> seriesDetailDtoList = seriesDetailComponent.getList(seriesIdList).join();
        return seriesDetailDtoList.stream()
                .filter(Objects::nonNull)
                .filter(SeriesDetailDto::getIsNewCar)
                .map(SeriesDetailDto::getId)
                .toList()
                .toString();
    }

    @Autowired
    private SpecYearNewComponent specYearNewComponent;

    @Autowired
    private SpecUsedCarPriceComponent specUsedCarPriceComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "/component/key", produces = "application/json;charset=utf-8")
    public String getKey() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        return specYearNewComponent.get(7669).toString();
//        return specUsedCarPriceComponent.get(9713).toString();
//        return specCityAskPriceComponent.get(56263, 152500).toString();

//        Object bean = applicationContext.getBean("specCityAskPriceComponent");
//        Method method = SpecCityAskPriceComponent.class
//                .getDeclaredMethod("makeParam", int.class, int.class);
//        method.setAccessible(true);
//        return specCityAskPriceComponent.getKey(
//                (TreeMap<String, Object>) method.invoke(bean, 56263, 152500));

        Method method = specCityAskPriceComponent.getClass()
                .getDeclaredMethod("makeParam", int.class, int.class);
        method.setAccessible(true);
        return specCityAskPriceComponent.getKey((TreeMap<String, Object>)
                method.invoke(specCityAskPriceComponent, 56263, 152500));

    }

    @Autowired
    CitySortHangqingComponent citySortHangqingComponent;

    @GetMapping(value = "/hangqing/refreshAll", produces = "application/json;charset=utf-8")
    public String refreshAll(int cityId) {
        citySortHangqingComponent.refreshAllCity(cityId, System.out::println);
        return "success";
    }

    @GetMapping(value = "/hangqing/clearCache", produces = "application/json;charset=utf-8")
    public String clearCache(int cityId) {
        citySortHangqingComponent.clearCache(cityId);
        return "success";
    }

    @Autowired
    SpecCityPriceHisComponent specCityPriceHisComponent;

    @GetMapping(value = "/hangqing/priceHis/refreshAll", produces = "application/json;charset=utf-8")
    public String priceHisRefreshAll(int cityId) {
        specCityPriceHisComponent.refreshAllCityTest(cityId, System.out::println);
        return "success";
    }

    @PostMapping(value = "/hangqing/priceHis/updateTemp", produces = "application/json;charset=utf-8")
    public String updateTemp(@RequestBody UpdateTempDto dto) {
        specCityPriceHisComponent.updateTemp(dto.getSpecId(), dto.getCityId(), dto.getDtoList());
        return "success";
    }

    @Data
    public static class UpdateTempDto {
        int specId;
        int cityId;
        List<SpecCityPriceHisDto> dtoList;
    }


//    @GetMapping(value = "/hangqing/spec/price/test", produces = "application/json;charset=utf-8")
//    public String hangqingSpecPriceTest() {
//        citySeriesHangqingSpecPriceComponent.test();
//        return "s";
//    }
    @GetMapping("/testJob")
    @ResponseBody
    public String testJob() {
        List<String> jobList = new ArrayList<>();
        jobList.add("brandInfoAllJob");
        jobList.forEach(job -> {
            try {
                IJobHandler jobHandler = this.jobMap.get(job);
                if (jobHandler != null) {
                    jobHandler.execute("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "ok";
    }





    @PostMapping(value = "/megadata/init", produces = "application/json;charset=utf-8")
    public String initMegaData(@RequestBody MegaDataDto megaDataDto) {
        log.info("init megadata---->>{}", megaDataDto.getSeriesId());
        stringRedisTemplate.opsForValue().set("app-cars-megadata:" + megaDataDto.getSeriesId(), JsonUtil.toString(megaDataDto));
        return "ok";
    }

    @Autowired
    private HqPicMapper hqPicMapper;

    @GetMapping(value = "/hqpic/test", produces = "application/json;charset=utf-8")
    public String test() {
//        Map<Integer, String> map = hqPicMapper.getHqPhotoSubtype();
//        System.out.println(map.toString());
//        return map.toString();
        return null;
    }

    @Autowired
    private HqPicDataComponent hqPicDataComponent;

    @Autowired
    private HqPhotoComponent hqPhotoComponent;

    @Autowired
    private SeriesYearCityPriceComponent seriesYearCityPriceComponent;

    @GetMapping(value = "/hqpic/picdata/test", produces = "application/json;charset=utf-8")
    public String picdatatest() {
        hqPicDataComponent.refreshAll(System.out::println);
        hqPhotoComponent.refreshAll(System.out::println);
        return "success";
    }

    @GetMapping(value = "/ershou-price/test", produces = "application/json;charset=utf-8")
    public Object picdatatest(@RequestParam int sid,@RequestParam int cityId) {
        return seriesYearCityPriceComponent.getByCity(sid,cityId);
    }

}
