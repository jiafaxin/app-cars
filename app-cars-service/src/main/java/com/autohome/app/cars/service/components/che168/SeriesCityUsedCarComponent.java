package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.DsabClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesUsedCarInfo;
import com.autohome.app.cars.service.components.misc.dtos.SeriesCityTabDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 何峰
 * 二手车分城市链接
 */
@Component
@DBConfig(tableName = "series_city_usedcar")
public class SeriesCityUsedCarComponent extends BaseComponent<SeriesCityUsedCarDto> {
    @Autowired
    Api2scautork2Client api2scautork2Client;

    @Autowired
    DsabClient dsabClient;

    @Autowired
    SeriesMapper seriesMapper;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";


    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityUsedCarDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        //List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        //Map<Integer, Integer> seriesBrandMap = allSeries.stream().collect(Collectors.toMap(SeriesEntity::getId, SeriesEntity::getBrandId));

        AtomicInteger apiError = new AtomicInteger(0);

        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            SeriesCityUsedCarDto dto = new SeriesCityUsedCarDto();

            CompletableFuture<Void> carCountFuture = api2scautork2Client.getCarCount(seriesId, cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    apiError.incrementAndGet();
                    return;
                }

                if (StringUtils.isNotEmpty(data.getResult().getTitle())) {
                    dto.setTitle(data.getResult().getTitle());
                    dto.setUrl(data.getResult().getUrl());
                } else {
                    dto.setTitle("");
                    dto.setUrl("");
                }
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            //todo speclist 需要回归下车系页的二手车链接
//            Integer brandId = seriesBrandMap.get(seriesId);
//            CompletableFuture<Void> seriesSpecListFuture = dsabClient.getSeriesSpecListJumpInfo(102, 3, cityId, seriesId, brandId).thenAccept(data -> {
//                if (data == null || data.getReturncode() != 0) {
//                    apiError.incrementAndGet();
//                    return;
//                }
//
//                if (StringUtils.isNotEmpty(data.getResult().getJumpurllist())) {
//                    dto.setSeriesSpecMoreUrl(data.getResult().getJumpurllist());
//                } else {
//                    dto.setSeriesSpecMoreUrl("");
//                }
//            }).exceptionally(e -> {
//                xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
//                return null;
//            });

            CompletableFuture.allOf(carCountFuture).join();

            if (apiError.get() > 0) {
                xxlLog.accept(seriesId + "-" + cityId + "失败");
                return;
            }
            if (StringUtils.isEmpty(dto.getTitle())) {
                delete(makeParam(seriesId, cityId));
            } else {
                update(makeParam(seriesId, cityId), dto);
            }
        }, xxlLog);
    }
}
