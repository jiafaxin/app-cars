package com.autohome.app.cars.service.components.dataopen;

import com.autohome.app.cars.apiclient.openApi.DataOpenApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dataopen.dtos.SeriesRecommendLikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
//@DBConfig(tableName = "series_recommend_like")
public class SeriesRecommendLikeComponent extends BaseComponent<SeriesRecommendLikeDto> {

    static String paramName = "seriesId";

    @Autowired
    DataOpenApiClient dataOpenApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesRecommendLikeDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(30,seriesId->{
            String ext = URLEncoder.encode("{\"userip\":\"\",\"series\":" + seriesId + ",\"type\":1}");
            dataOpenApiClient.getRecommendLikeSeriesList(90100099, 5, ext).thenAccept(userlike -> {
                SeriesRecommendLikeDto dto = new SeriesRecommendLikeDto();
                dto.setSeriesId(seriesId);

                if (userlike != null && userlike.getResult() != null && userlike.getResult().getItemlist() != null
                        && userlike.getResult().getItemlist().size() > 0) {

                    boolean flag = userlike.getResult().getItemlist().stream().anyMatch(item -> item.getResourceobj() != null
                            && item.getResourceobj().getBiz_type() == 54
                            && item.getObjlist() != null && !item.getObjlist().isEmpty());

                    dto.setHasData(flag ? 1 : 0);
                }
                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        },xxlLog);
    }
}
