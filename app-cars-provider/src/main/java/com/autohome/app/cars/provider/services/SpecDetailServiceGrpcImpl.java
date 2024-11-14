package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.DubboSpecDetailServiceTriple;
import autohome.rpc.car.app_cars.v1.carbase.SpecBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SpecBaseInfoResponse;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.services.SpecInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@DubboService
@RestController
@Slf4j
public class SpecDetailServiceGrpcImpl extends DubboSpecDetailServiceTriple.SpecDetailServiceImplBase {

    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SpecInfoService specInfoService;

    /**
     * 从11.61.5版本 开始接入
     */
    @Override
    @GetMapping(value = "/carbase/specsummary/carsbaseinfo", produces = "application/json;charset=utf-8")
    public SpecBaseInfoResponse specBaseInfo(SpecBaseInfoRequest request) {
        if (request.getSpecid() <= 0) {
            return SpecBaseInfoResponse.newBuilder().setReturnCode(101).setReturnMsg("传入参数有误").build();
        }
        SpecBaseInfoResponse.Result.Builder result = SpecBaseInfoResponse.Result.newBuilder();

        // 优先通过获取车型基本信息dto，后续信息拼装会依赖车型基本信息
        SpecDetailDto specDetailDto = specDetailComponent.get(request.getSpecid()).join();
        if (Objects.isNull(specDetailDto)) {
            return SpecBaseInfoResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("车型不存在")
                    .build();
        }

        List<CompletableFuture> tasks = new ArrayList<>();
        CompletableFuture<SpecCityAskPriceDto> specCityAskPriceDtoFuture = specCityAskPriceComponent.get(request.getSpecid(), request.getCityid());
        // 车型主要信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildSpecBaseInfo(specDetailDto, specCityAskPriceDtoFuture, request)
                        .thenAccept(result::setSpecbaseinfo).exceptionally(e -> {
                            log.error("车型页-车型基本信息组装异常, specDetailDto={}", specDetailDto, e);
                            return null;
                        }),
                ThreadPoolUtils.defaultThreadPoolExecutor));
        // 车型图片信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildSpecPicInfo(specDetailDto, request)
                .thenAccept(result::setSpecpicinfo).exceptionally(e -> {
                    log.error("车型页-车型图片信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));
        // pricelist信息（计算器、口碑、车主提车价、二手车价）
        if ("1".equals(request.getFuncabtest())
                && CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.64.0")) {
            // 新版车型页且版本大于11.64.0，则无需这四个大块，糖豆全部由糖豆接口返回
            // https://doc.autohome.com.cn/docapi/page/share/share_uQ7nIwuMDY
        } else {
            tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildSpecPriceList(specDetailDto, request)
                    .thenAccept(e -> result.addAllPricelist(e))
                    .exceptionally(e -> {
                        log.error("车型页-车型图片信息组装异常, specDetailDto={}", specDetailDto, e);
                        return null;
                    }), ThreadPoolUtils.defaultThreadPoolExecutor));
        }
        // dealermodules信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildDealerModules(request)
                .thenAccept(result::addAllDealermodules)
                .exceptionally(e -> {
                    log.error("车型页-dealermodules信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));
        // 车辆参数配置信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildCarParamConfig(specDetailDto, request.getPm())
                .thenAccept(result::setCarparmconfig)
                .exceptionally(e -> {
                    log.error("车型页-车型参数配置信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));
        // 实测信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildPracticalInfo(specDetailDto)
                .thenAccept(result::setPracticalinfo)
                .exceptionally(e -> {
                    log.error("车型页-车型实测置信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));
        // 经销商价格信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildPriceInfo(specDetailDto, request)
                .thenAccept(result::setPriceinfo)
                .exceptionally(e -> {
                    log.error("车型页-车型经销商价格信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));
        //tabinfo信息
        tasks.add(specInfoService.buildTabInfo(specDetailDto, request).thenAccept(result::addAllTabinfo));
        // zixun信息
        tasks.add(CompletableFuture.runAsync(() -> specInfoService.buildZixunInfo(specDetailDto.getSeriesId(),
                        specDetailDto.getSpecId(), request.getCityid(), request.getPm(), request.getZixunabtest())
                .thenAccept(result::setZixuninfo)
                .exceptionally(e -> {
                    log.error("车型页-车型咨询信息组装异常, specDetailDto={}", specDetailDto, e);
                    return null;
                }), ThreadPoolUtils.defaultThreadPoolExecutor));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return SpecBaseInfoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("success")
                .setResult(result)
                .build();
    }

}