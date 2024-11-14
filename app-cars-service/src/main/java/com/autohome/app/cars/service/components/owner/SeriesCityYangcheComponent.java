package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.CardResult;
import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.owner.dtos.SeriesCityYangche;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * TODO 周宇想办法处理，年后找
 * http://ownermp.corpautohome.com/ownerhotapi/usecar/portal/card?seriesid=66&pid=110000&cityid=110100&app_version=11.58.999
 */
@Component
@DBConfig(tableName = "series_city_yangche")
public class SeriesCityYangcheComponent extends BaseComponent<SeriesCityYangche> {

    @Autowired
    OwnerApiClient ownerApiClient;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityYangche> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            int provinceId = CityUtil.getProvinceId(cityId);
            ownerApiClient.getCard(seriesId, provinceId, cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null) {
                    delete(makeParam(seriesId,cityId));
                    return;
                }

                CardResult.Item item = data.getResult().getCards().stream().filter(x -> "YCCB".equals(x.getCode())).findFirst().orElse(null);
                if (item == null ) {
                    delete(makeParam(seriesId,cityId));
                    return;
                }

                SeriesCityYangche dto = new SeriesCityYangche();
                dto.setSeriesId(seriesId);
                dto.setCityId(cityId);
                dto.setData(item.getData());
                dto.setAppHref(item.getAppHref());

                update(makeParam(seriesId, cityId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }
}
