package com.autohome.app.cars.service.components.jiage;

import com.autohome.app.cars.apiclient.jiage.JiageApiClient;
import com.autohome.app.cars.apiclient.jiage.dtos.SeriesBottomPriceResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.jiage.dtos.SeriesJiageDto;
import com.autohome.app.cars.service.common.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 */
@Component
@DBConfig(tableName = "series_jiage")
public class SeriesJiageComponent extends BaseComponent<SeriesJiageDto> {

    static String paramName = "seriesId";

    @Autowired
    JiageApiClient jiageApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesJiageDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {

        loopSeries(30,seriesId->{
            jiageApiClient.getSeriesBottomPrice(seriesId+"").thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null ||data.getResult().size()==0 || data.getResult().get(0)==null) {
                    delete(makeParam(seriesId));
                    return;
                }

                SeriesBottomPriceResult item = data.getResult().get(0);
                SeriesJiageDto dto = new SeriesJiageDto();
                dto.setSeriesId(seriesId);
                dto.setSubTitle(item.getSubTitle());
                dto.setPrice(item.getPrice());
                dto.setOwnerPrice(item.getOwnerPrice());
                dto.setTotal(item.getTotal());

                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        },xxlLog);

    }

}
