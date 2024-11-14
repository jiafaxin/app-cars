package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carmiddle.*;
import com.autohome.app.cars.common.enums.AppIdEnum;
import com.autohome.app.cars.common.utils.Md5Util;
import com.autohome.app.cars.service.services.CarMiddleService;
import com.google.protobuf.Descriptors;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeMap;

@DubboService
@Slf4j
@RestController
public class CarMiddleServiceGrpcImpl extends DubboCarMiddleServiceTriple.CarMiddleServiceImplBase {

    @Autowired
    private CarMiddleService carMiddleService;

    @CrossOrigin
    @GetMapping(value = "/carMiddle/getBrandInfoAll", produces = "application/json;charset=UTF-8")
    @Override
    public BrandInfoAllResponse getBrandInfoAll(BrandInfoAllRequest request){
        BrandInfoAllResponse response = carMiddleService.getBrandInfoAll(request);
        return response;
    }

    @CrossOrigin
    @GetMapping(value = "/carMiddle/getSeriesListByBrandId",produces = "application/json;charset=UTF-8")
    @Override
    public SeriesListByBrandIdResponse getSeriesListByBrandId(SeriesListByBrandIdRequest request) {
        SeriesListByBrandIdResponse response = carMiddleService.getSeriesListByBrandId(request);
        return response;
    }

    @CrossOrigin
    @GetMapping(value = "/carMiddle/getSpecListBySeriesId",produces = "application/json;charset=UTF-8")
    @Override
    public SpecListBySeriesIdResponse getSpecListBySeriesId(SpecListBySeriesIdRequest request) {
        SpecListBySeriesIdResponse response = carMiddleService.getSpecListBySeriesId(request);
        return response;
    }

    @GetMapping(value = "/carMiddle/getRecommendationList",produces = "application/json;charset=UTF-8")
    @Override
    public RecommendationListResponse getRecommendationList(RecommendationListRequest request) {
        return carMiddleService.getBrandTopRecommendation(request);
    }
}