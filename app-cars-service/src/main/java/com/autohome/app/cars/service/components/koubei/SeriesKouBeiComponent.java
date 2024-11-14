package com.autohome.app.cars.service.components.koubei;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.common.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * http://koubei.api.sjz.autohome.com.cn/api/series/carbean?_appid=app&seriesid=18 (result.scheme)
 */
@Component
@DBConfig(tableName = "series_koubei")
public class SeriesKouBeiComponent extends BaseComponent<SeriesKouBeiDto> {

    static String paramName = "seriesId";

    @Autowired
    KoubeiApiClient koubeiApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesKouBeiDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesKouBeiDto>> getList(List<Integer> seriesIdList) {
        return baseGetListAsync(seriesIdList.stream().map(this::makeParam).collect(Collectors.toList()));
    }


    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(10, seriesId -> {
            koubeiApiClient.getSeriesBean(seriesId).thenCombineAsync(koubeiApiClient.getSeriesScoreUserNum(seriesId), (data, scoreInfo) -> {
                if ((data == null || data.getReturncode() != 0) && (scoreInfo == null || scoreInfo.getReturncode() != 0)) {
                    return null;
                }

                if ((data!=null && data.getResult() == null) || (scoreInfo!=null && scoreInfo.getResult() == null)) {
                    delete(makeParam(seriesId));
                    return null;
                }
                SeriesKouBeiDto dto = getFromRedis(makeParam(seriesId));
                if (dto == null) {
                    dto = new SeriesKouBeiDto();
                }
                dto.setSeriesId(seriesId);
                if (data != null && data.getReturncode() == 0 && data.getResult() != null) {
                    dto.setBean(new SeriesKouBeiDto.Bean() {{
                        setAppScheme(data.getResult().getScheme());
                        setSubTitle(data.getResult().getSubTitle());
                        setScoreTitle(data.getResult().getScoreTitle());
                    }});
                }

                if (scoreInfo != null && scoreInfo.getReturncode() == 0 && scoreInfo.getResult() != null) {
                    dto.setScoreInfo(new SeriesKouBeiDto.ScoreInfo() {{
                        setEvalCount(scoreInfo.getResult().getEvalCount());
                        setAverage(scoreInfo.getResult().getAverage());
                    }});
                }
                update(makeParam(seriesId), dto);
                return null;
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        }, xxlLog);
    }

}
