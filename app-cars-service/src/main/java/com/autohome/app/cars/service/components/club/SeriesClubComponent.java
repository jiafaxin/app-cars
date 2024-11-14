package com.autohome.app.cars.service.components.club;

import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.club.dtos.SeriesClub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_club")
@Slf4j
public class SeriesClubComponent extends BaseComponent<SeriesClub>  {

    @Autowired
    ClubApiClient clubApiClient;

    final static String paramName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesClub> get(int seriesId){
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(28, seriesId -> {
            refreshOne(seriesId).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                log.error("同步失败" + seriesId, e);
                return null;
            });
        }, xxlLog);
    }

    CompletableFuture<Object> refreshOne(int seriesId) {
        return clubApiClient.getClubLatelyScoreForCar(seriesId).thenCombineAsync(clubApiClient.getSeriesQaInfoForCar(seriesId), (data, qa) -> {
            if ((data == null || data.getReturncode() != 0) && (qa == null || qa.getReturncode() != 0)) {
                return null;
            }

            if ((data != null && data.getReturncode() == 0 && data.getResult() == null) && (qa != null && qa.getReturncode() == 0 && qa.getResult() == null)) {
                return null;
            }

            SeriesClub dto = new SeriesClub();
            dto.setSeriesId(seriesId);
            if(data != null && data.getReturncode() == 0 && data.getResult() != null) {
                dto.setJumpUrl(data.getResult().getJumpUrl());
                dto.setBbsName(data.getResult().getBbsName());
                dto.setSubTitle(data.getResult().getSubTitle());
            }

            if(qa != null && qa.getReturncode() == 0 && qa.getResult() != null) {
                dto.setQaJumpUrl(qa.getResult().getJumpUrl());
                dto.setQaSubTitle(qa.getResult().getSubTitle());
            }

            update(makeParam(seriesId), dto);
            return null;
        });
    }

}
