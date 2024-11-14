package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.owner.dtos.SeriesGaizhuang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 改装（李李佳年后出新接口，和城市、版本无关，按照车系取就行）
 * http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/candy?seriesid=66&pid=110000&cityid=110100&app_version=11.58.99
 */
@Component
@DBConfig(tableName = "series_gaizhuang")
public class SeriesGaizhuangComponent extends BaseComponent<SeriesGaizhuang> {

    @Autowired
    OwnerApiClient ownerApiClient;

    final static String paramName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesGaizhuang> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(30,seriesId->{
            ownerApiClient.getGaizhuang(seriesId).thenAccept(data -> {
                if(data==null ||data.getReturncode() != 0 ){
                    return;
                }

                if (data.getResult() == null || data.getResult().size() == 0 || data.getResult().get(0) == null) {
                    delete(makeParam(seriesId));
                    return;
                }
                SeriesGaizhuang dto = new SeriesGaizhuang();
                dto.setSeriesId(seriesId);
                dto.setAppHref(data.getResult().get(0).getAppHref());
                dto.setSubTitle(data.getResult().get(0).getSubtitle());
                dto.setTitle(data.getResult().get(0).getTitle());
                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        },xxlLog);
    }
}
