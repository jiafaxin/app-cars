package com.autohome.app.cars.service.components.club;

import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.ClubWendaResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.club.dtos.SeriesClubWendaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Component
@DBConfig(tableName = "series_club_wenda")
public class SeriesClubWendaComponent extends BaseComponent<SeriesClubWendaDto> {

    @SuppressWarnings("all")
    @Autowired
    ClubApiClient clubApiClient;

    final static String paramName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesClubWendaDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> clubApiClient.getSeriesClubWendaResult(seriesId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }
            if (data.getResult() == null) {
                delete(makeParam(seriesId));
                return;
            }

            ClubWendaResult result = data.getResult();
            SeriesClubWendaDto dto = new SeriesClubWendaDto();
            dto.setPageCount(result.getPageCount());
            dto.setPageIndex(result.getPageIndex());
            dto.setRowCount(result.getRowCount());
            update(makeParam(seriesId), dto);
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }).join(), xxlLog);
    }


}
