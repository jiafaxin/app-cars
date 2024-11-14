package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoResponse;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.service.components.dealer.SeriesCityCpsComponent;
import com.autohome.app.cars.service.components.subsidy.SpecCitySubsidyComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.app_cars.v1.miscs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DubboService
@Slf4j
@RestController
public class ButieServiceGrpcImpl extends DubboButieServiceTriple.ButieServiceImplBase {

    @Autowired
    SpecCitySubsidyComponent specCitySubsidyComponent;

    @Autowired
    SeriesCityCpsComponent seriesCityCpsComponent;

    @Override
    @GetMapping("/miscs/butie/series_city_price")
    public SeriesCityPriceResponse seriesCityPrice(SeriesCityPriceRequest request) {

        int price = specCitySubsidyComponent.getSeriesCityData(request.getSeriesId(),request.getCityId()).thenCombine(seriesCityCpsComponent.get(request.getSeriesId(), request.getCityId()),(subsidy,cps)->{
            if(subsidy==null||cps==null){
                return 0;
            }
            if(subsidy.getPrice()==0||cps.getPrice().intValue()==0){
                return 0;
            }
            return subsidy.getPrice() + cps.getPrice().intValue();
        }).exceptionally(e->{
            log.error("车展糖豆异常",e);
            return 0;
        }).join();

        return SeriesCityPriceResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("")
                .setResult(SeriesCityPriceResponse.Result.newBuilder().setPrice(price))
                .build();
    }

}