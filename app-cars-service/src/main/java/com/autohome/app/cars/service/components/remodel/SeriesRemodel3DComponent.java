package com.autohome.app.cars.service.components.remodel;

import com.autohome.app.cars.apiclient.remodel.RemodelApiClient;
import com.autohome.app.cars.apiclient.remodel.dtos.Remodel3DResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.remodel.dtos.SeriesRemodel3DDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车系改装3D -- 李李佳
 */
@Component
@DBConfig(tableName = "series_remodel_3d")
public class SeriesRemodel3DComponent extends BaseComponent<SeriesRemodel3DDto> {

    static String paramName = "seriesId";

    @Autowired
    RemodelApiClient remodelApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesRemodel3DDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> remodelApiClient.getInfoBySeries(seriesId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }
            if (data.getResult() == null || StringUtils.isEmpty(data.getResult().getJumpProjectUrl())) {
                delete(makeParam(seriesId));
                return;
            }

            Remodel3DResult result = data.getResult();
            SeriesRemodel3DDto dto = new SeriesRemodel3DDto();
            dto.setJumpUrl(result.getJumpUrl());
            dto.setStatus(result.getStatus());
            dto.setSeriesId(result.getSeriesId());
            dto.setModelId(result.getModelId());
            dto.setProjectUid(result.getProjectUid());
            dto.setJumpProjectUrl(result.getJumpProjectUrl());
            dto.setJumpPaintingUrl(result.getJumpPaintingUrl());
            dto.setImages(result.getImages());
            dto.setSmallImages(result.getSmallImages());

            update(makeParam(seriesId), dto);
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }), xxlLog);
    }
}