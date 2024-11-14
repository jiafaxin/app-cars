package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carext.*;
import com.autohome.app.cars.service.services.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zzli
 * @description : 车系动态频道
 * @date : 2024/9/4 14:40
 */
@DubboService
@RestController
@Slf4j
public class SeriesSubscribeServiceGrpcImpl extends DubboSeriesSubscribeServiceTriple.SeriesSubscribeServiceImplBase {

    @Autowired
    SubscribeService subscribeService;

    /**
     * 动态消息列表接口
     */
    @Override
    @GetMapping(value = "/carext/seriessubscribe/pagegetlist", produces = "application/json;charset=utf-8")
    public SeriesSubscribePageGetResponse pageGetList(SeriesSubscribePageGetRequest request) {
        try {
            return subscribeService.pageGetList(request);
        } catch (Exception e) {
            log.error("pageGetList error", e);
            return SeriesSubscribePageGetResponse.newBuilder()
                    .setReturnCode(101)
                    .setResult(SeriesSubscribePageGetResponse.Result.newBuilder())
                    .setReturnMsg("fail")
                    .build();
        }
    }

    /**
     * 订阅车系列表tab接口
     */
    @Override
    @GetMapping(value = "/carext/seriessubscribe/taginfolist", produces = "application/json;charset=utf-8")
    public SeriesSubscribeTagInfoResponse tagInfoList(SeriesSubscribeTagInfoRequest request) {
        try {
            return subscribeService.tagInfoList(request);
        } catch (Exception e) {
            log.error("tagInfoList error", e);
            return SeriesSubscribeTagInfoResponse.newBuilder()
                    .setReturnCode(101)
                    .setResult(SeriesSubscribeTagInfoResponse.Result.newBuilder())
                    .setReturnMsg("fail")
                    .build();
        }
    }

    /**
     * 无动态推荐车系列表接口
     */
    @Override
    @GetMapping(value = "/carext/seriessubscribe/recommendedcarlist", produces = "application/json;charset=utf-8")
    public SeriesSubscribeRecommendedCarResponse recommendedCarList(SeriesSubscribeRecommendedCarRequest request) {
        try {
            return subscribeService.getRecommendedCarList(request);
        } catch (Exception e) {
            log.error("recommendedCarList error", e);
            return SeriesSubscribeRecommendedCarResponse.newBuilder()
                    .setReturnCode(101)
                    .setResult(SeriesSubscribeRecommendedCarResponse.Result.newBuilder())
                    .setReturnMsg("fail")
                    .build();
        }

    }
}
