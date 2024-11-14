package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.DubboSeriesCompareServiceTriple;
import autohome.rpc.car.app_cars.v1.carbase.GetBuyCarDiscountInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.GetBuyCarDiscountInfoResponse;
import autohome.rpc.car.app_cars.v1.carbase.GetSeriesDiscountInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.GetSeriesDiscountInfoResponse;
import autohome.rpc.car.app_cars.v1.carbase.SeriesCompareRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesCompareResponse;
import com.autohome.app.cars.service.services.SeriesCompareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DubboService
@RestController
@Slf4j
public class SeriesCompareServiceGrpcImpl extends DubboSeriesCompareServiceTriple.SeriesCompareServiceImplBase {

    @Autowired
    private SeriesCompareService seriesCompareService;

    @Override
    @GetMapping(value = "/carbase/selectcarportal/seriescompare/getseriescompareinfo", produces = "application/json;charset=utf-8")
    public SeriesCompareResponse getSeriesCompareInfo(SeriesCompareRequest request) {
        SeriesCompareResponse response = seriesCompareService.getSeriesCompare(request);
        return response;
    }

    @Override
    @GetMapping(value = "/carbase/selectcarportal/seriescompare/getbuycardiscountinfo", produces = "application/json;charset=utf-8")
    public GetBuyCarDiscountInfoResponse getBuyCarDiscountInfo(GetBuyCarDiscountInfoRequest request) {
        GetBuyCarDiscountInfoResponse response = seriesCompareService.getBuyCarDiscountInfo(request);
        return response;
    }

    @Override
    @GetMapping(value = "/carbase/selectcarportal/seriescompare/getseriesdiscountinfo", produces = "application/json;charset=utf-8")
    public GetSeriesDiscountInfoResponse getSeriesDiscountInfo(GetSeriesDiscountInfoRequest request) {
        GetSeriesDiscountInfoResponse response = seriesCompareService.getSeriesDiscountInfo(request);
        return response;
    }

}