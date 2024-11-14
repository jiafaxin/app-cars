package com.autohome.app.cars.service.components.misc;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.PlayCarCardResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesCityTabDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesHotCommentDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesTabDto;
import com.autohome.app.cars.service.components.remodel.SeriesRemodel3DComponent;
import com.autohome.app.cars.service.components.remodel.dtos.SeriesRemodel3DDto;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 车系tab是否有数据判断
 * 每天更新，1小时更新完
 */
@Component
@DBConfig(tableName = "series_city_tab")
public class SeriesCityTabComponent extends BaseComponent<SeriesCityTabDto> {
    @Autowired
    Api2scautork2Client api2scautork2Client;

    @Autowired
    OwnerApiClient ownerApiClient;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SeriesRemodel3DComponent seriesRemodel3DComponent;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";


    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityTabDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        Map<Integer, SeriesDetailDto> seriesMap = new HashMap<>();
        Map<Integer, SeriesRemodel3DDto> seriesRemodel3DMap = new HashMap<>();

        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            SeriesDetailDto seriesDetailDto;
            if (seriesMap.containsKey(seriesId)) {
                seriesDetailDto = seriesMap.get(seriesId);
            } else {
                seriesDetailDto = seriesDetailComponent.get(seriesId);
                seriesMap.put(seriesId, seriesDetailDto);
            }

            SeriesRemodel3DDto seriesRemodel3DDto;
            if (seriesRemodel3DMap.containsKey(seriesId)) {
                seriesRemodel3DDto = seriesRemodel3DMap.get(seriesId);
            } else {
                seriesRemodel3DDto = seriesRemodel3DComponent.get(seriesId).join();
                seriesRemodel3DMap.put(seriesId, seriesRemodel3DDto);
            }

            SeriesCityTabDto dto = new SeriesCityTabDto();
            dto.setSeriesId(seriesId);

            CompletableFuture<Void> erShouCheFuture = api2scautork2Client.getRecommendCars(seriesId, cityId, "").thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult().getCarcount() > 0) {
                    dto.setHasErShouData(1);

                } else {
                    dto.setHasErShouData(0);
                }
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            CompletableFuture<Void> playCarFuture = ownerApiClient.getPlayCarCard(seriesId, seriesDetailDto.getLevelId(), cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }
                if (data.getResult() == null || data.getResult().getList() == null || data.getResult().getList().isEmpty()) {
                    return;
                }

                if (data.getResult().getTabs() != null && data.getResult().getTabs().stream().anyMatch(x -> x.getTabName().equals("改装")) && seriesRemodel3DDto != null) {
                    dto.setHasGaizhuangWithRefitData(1);
                } else {
                    dto.setHasGaizhuangWithRefitData(0);
                }
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            CompletableFuture.allOf(erShouCheFuture, playCarFuture).join();
            update(makeParam(seriesId, cityId), dto);
        }, xxlLog);
    }
}
