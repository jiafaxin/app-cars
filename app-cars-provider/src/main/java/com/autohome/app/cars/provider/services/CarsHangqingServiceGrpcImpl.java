package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carext.*;
import com.autohome.app.cars.service.services.CarsHangqingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@DubboService
@RestController
@Slf4j
public class CarsHangqingServiceGrpcImpl extends DubboCarsHangqingServiceTriple.CarsHangqingServiceImplBase {

    @Autowired
    private CarsHangqingService carsHangqingService;

    @Override
    @GetMapping(value = "/carext/carshangqing/list", produces = "application/json;charset=utf-8")
    public CarsHangqingPageGetResponse pageGetList(CarsHangqingPageGetRequest request) {
        try {
            CarsHangqingPageGetResponse.Result.Builder resultBuilder =
                    carsHangqingService.getResult(request.getCityid(),
                            request.getPageindex(),
                            request.getPagesize(),
                            request.getBrand(),
                            request.getLevelid(),
                            request.getPrice(),
                            request.getEnergytype(),
                            request.getOrderid()).join();
            if (Objects.nonNull(resultBuilder) && !CollectionUtils.isEmpty(resultBuilder.getCardlistList())) {
                return CarsHangqingPageGetResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("success")
                        .setResult(resultBuilder)
                        .build();
            } else {
                return CarsHangqingPageGetResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("无车辆行情数据")
                        .setResult(resultBuilder)
                        .build();
            }
        } catch (Exception e) {
            log.error("获取车辆行情异常", e);
            return CarsHangqingPageGetResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("获取车辆行情异常")
                    .build();
        }
    }

    @Override
    @GetMapping(value = "/carext/carshangqing/searchoptions", produces = "application/json;charset=utf-8")
    public CarsHangqingSearchOptionsResponse getSearchOptions(CarsHangqingSearchOptionsRequest request) {
        CarsHangqingSearchOptionsResponse.Result.Builder result= carsHangqingService.getSearchOptions(request);
        if (result!=null){
            return CarsHangqingSearchOptionsResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(result)
                    .build();
        }else {
            return CarsHangqingSearchOptionsResponse.newBuilder()
                    .setReturnCode(1)
                    .setReturnMsg("暂无数据")
                    .build();
        }
    }
}