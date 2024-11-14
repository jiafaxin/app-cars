package com.autohome.app.cars.provider.services;

import java.util.concurrent.CompletableFuture;

import autohome.rpc.car.app_cars.v1.carext.DubboSeriesTabServiceTriple;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabRequest;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabResponse;
import com.autohome.app.cars.provider.config.SimplifyJson;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.services.SeriesTabService;
import com.autohome.app.cars.service.services.enums.SeriesTabTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DubboService
@RestController
@Slf4j
public class SeriesTabServiceGrpcImpl extends DubboSeriesTabServiceTriple.SeriesTabServiceImplBase {

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesTabService seriesTabService;

    @Override
    @GetMapping(value = {"/carext/seriessummary/tabcard", "/carstreaming/seriessummary/tabcard"}, produces = "application/json;charset=utf-8")
    @SimplifyJson
    public SeriesTabResponse getTabCard(SeriesTabRequest request) {
        //newsummaryab实验固化成“0” @王宇宸
        SeriesTabRequest.Builder builder = request.toBuilder();
        builder.setNewsummaryab("0");
        request = builder.build();
        //车系详情
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());

        if (seriesDetailDto == null) {
            return SeriesTabResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("车系不存在")
                    .build();
        }

        CompletableFuture<SeriesTabResponse.Result.Builder> result = CompletableFuture.completedFuture(null);
        SeriesTabTypeEnum tabType = SeriesTabTypeEnum.of(request.getTabid());
        switch (tabType) {
            case NEWS:
                result = seriesTabService.getNewsTab(seriesDetailDto, request);
                break;
            case CLUB:
                result = seriesTabService.getSeriesClubCard(request);
                break;
            case KOUBEI:
                result = seriesTabService.getKouBeiTab(seriesDetailDto, request);
                break;
            case PLAYCAR:
                result = seriesTabService.getPlayCarTab(seriesDetailDto, request);
                break;
            case USECAR:
                result = seriesTabService.getUseCarTab(seriesDetailDto, request);
                break;
            case USEDCAR:
                result = seriesTabService.getUsedCarTab(seriesDetailDto, request);
                break;
            case SAMELEVEL:
                result = seriesTabService.getSameLevelTab(seriesDetailDto, request);
                break;
            case HOTCOMMENT:
                result = seriesTabService.getHotCommentTab(seriesDetailDto, request);
                break;
        }

        return result.thenApply(x->{
            if (x == null) {
                return SeriesTabResponse.newBuilder().setReturnCode(0).setResult(SeriesTabResponse.Result.newBuilder().build()).setReturnMsg("暂无数据").build();
            }
            return SeriesTabResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(x)
                    .build();
        }).join();

    }
}