package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carext.DubboSeriesStreamingServiceTriple;
import autohome.rpc.car.app_cars.v1.carext.SeriesMvpRequest;
import autohome.rpc.car.app_cars.v1.carext.SeriesMvpResponse;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabResponse;
import com.autohome.app.cars.apiclient.bfai.dtos.SSeriesSortListResult;
import com.autohome.app.cars.provider.config.SimplifyJson;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.services.SeriesListService;
import com.autohome.app.cars.service.services.SeriesMvpService;
import com.autohome.app.cars.service.services.SeriesTabService;
import com.autohome.app.cars.service.services.enums.SeriesTabTypeEnum;
import com.google.protobuf.AbstractMessageLite;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@DubboService
@RestController
@Slf4j
public class SeriesStreamingServiceGrpcImpl extends DubboSeriesStreamingServiceTriple.SeriesStreamingServiceImplBase {

    @Autowired
    SeriesMvpService seriesMvpService;
    @Override
    @GetMapping(value = {"/carstreaming/seriessummary/mvpinfo"}, produces = "application/json;charset=utf-8")
    public SeriesMvpResponse getMvpInfo(SeriesMvpRequest request) {
        //车系详情
        CompletableFuture<SeriesMvpResponse.Result.Builder> seriesMvpInfo = seriesMvpService.getSeriesMvpInfo(request.getFrom(), request.getCityid(), request.getSeriesid(), request.getSpecid(),request.getDeviceid(),request.getAbtest(),request);
        return seriesMvpInfo.thenApply(x->{
            if (x == null) {
                return SeriesMvpResponse.newBuilder().setReturnCode(0).setResult(SeriesMvpResponse.Result.newBuilder().build()).setReturnMsg("暂无数据").build();
            }
            return SeriesMvpResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(x)
                    .build();
        }).join();
    }
}