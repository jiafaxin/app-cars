package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.*;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.services.SeriesSpecListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DubboService
@RestController
@Slf4j
public class SeriesSpecListServiceGrpcImpl extends DubboSeriesSpecListServiceTriple.SeriesSpecListServiceImplBase {
    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesSpecListService seriesSpecListService;

    @Override
    @GetMapping(value = "/carbase/seriessummary/specbaselist", produces = "application/json;charset=utf-8")
    public SeriesSpecListBaseInfoResponse seriesSpecListBaseInfo(SeriesSpecListBaseInfoRequest request) {
        //log.info("seriesSpecListBaseInfo request: {}", request);
        if (request.getSeriesid() <= 0) {
            return SeriesSpecListBaseInfoResponse.newBuilder().setReturnCode(101).setReturnMsg("传入参数有误").build();
        }

        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());
        if (seriesDetailDto == null) {
            return SeriesSpecListBaseInfoResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("车系不存在")
                    .build();
        }

        SeriesSpecListBaseInfoResponse.Result.Builder result = SeriesSpecListBaseInfoResponse.Result.newBuilder();
        List<CompletableFuture> tasks = new ArrayList<>();

        //车型列表
        tasks.add(seriesSpecListService.getSpecInfo(seriesDetailDto, request).thenAccept(result::setSpecinfo));

        if (seriesDetailDto.getState() == 40) {
            //我要卖车
            tasks.add(CompletableFuture.supplyAsync(() -> seriesSpecListService.getSaleCarInfo(), ThreadPoolUtils.defaultThreadPoolExecutor).thenAccept(result::setSalecarinfo));
        }

        //预约试驾等其它，车型列表下的卡片
        tasks.add(seriesSpecListService.getSpecBottomList(seriesDetailDto, request.getCityid(), request.getPm()).thenAccept(x -> {
            if (x != null) {
                result.addSpecbottomlist(x);
            }
        }));

        //等待所有任务完成
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> SeriesSpecListBaseInfoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("success")
                .setResult(result)
                .build()
        ).exceptionally(e -> {
            log.error("specBaseList error:" + request.getSeriesid(), e);
            return SeriesSpecListBaseInfoResponse.newBuilder()
                    .setReturnCode(101)
                    .setReturnMsg("fail")
                    .build();
        }).join();
    }
}
