package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.club.DriveApiClient;
import com.autohome.app.cars.apiclient.club.dtos.TestDriveCityResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesDriveDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 上门试驾业务
 */
@Component
@DBConfig(tableName = "series_drive")
@Slf4j
public class SeriesDriveComponent extends BaseComponent<SeriesDriveDto> {

    @Autowired
    DriveApiClient driveApiClient;

    final static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesDriveDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSeries(totalMinutes, (seriesId) -> {
            CompletableFuture<BaseModel<List<TestDriveCityResult>>> d1 = driveApiClient.getSeriesDriveCities(seriesId, 1).exceptionally(e -> {
                xxlLog.accept("获取车系试驾城市报错" + seriesId + " - 1");
                log.error("获取车系试驾城市报错1", e);
                return null;
            });
            CompletableFuture<BaseModel<List<TestDriveCityResult>>> d2 = driveApiClient.getSeriesDriveCities(seriesId, 2).exceptionally(e -> {
                xxlLog.accept("获取车系试驾城市报错" + seriesId + " - 2");
                log.error("获取车系试驾城市报错2", e);
                return null;
            });

            d1.thenCombineAsync(d2, (t, ht) -> {
                //如果接口都报错了，不处理
                if (t == null && ht == null) {
                    return null;
                }
                //如果接口都没报错，且没有数据，则删除
                if (t != null && t.getReturncode() == 0 && t.getResult() != null && t.getResult().size() == 0 &&
                        ht != null && ht.getReturncode() == 0 && ht.getResult() != null && ht.getResult().size() == 0) {
                    delete(makeParam(seriesId));
                    return null;
                }

                SeriesDriveDto dto = null;
                //如果两个有一个报错，则读取缓存的数据
                if (t == null || ht == null) {
                    dto = baseGet(makeParam(seriesId));
                }
                if(dto==null){
                    dto = new SeriesDriveDto();
                    dto.setSeriesId(seriesId);
                }

                if (t != null) {
                    if (t.getReturncode() == 0 && t.getResult() != null) {
                        dto.setTestDriveCitys(t.getResult().stream().map(x -> x.getId()).collect(Collectors.toList()));
                    } else {
                        dto.setTestDriveCitys(new ArrayList<>());
                    }
                }

                if (ht != null) {
                    if (ht.getReturncode() == 0 && ht.getResult() != null) {
                        dto.setHomeTestDriveCitys(ht.getResult().stream().map(x -> x.getId()).collect(Collectors.toList()));
                    } else {
                        dto.setHomeTestDriveCitys(new ArrayList<>());
                    }
                }

                if(dto.getTestDriveCitys().size()==0||dto.getHomeTestDriveCitys().size()==0){
                    delete(makeParam(seriesId));
                    return null;
                }

                update(makeParam(seriesId), dto);
                return null;
            }).exceptionally(e -> {
                xxlLog.accept("获取车系试驾城市报错" + seriesId + " - 1、2");
                log.error("获取车系试驾城市报错", e);
                return null;
            });

        }, xxlLog);
    }
}
