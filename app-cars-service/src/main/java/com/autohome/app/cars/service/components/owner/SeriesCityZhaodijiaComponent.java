package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.im.dtos.SeriesImResult;
import com.autohome.app.cars.apiclient.owner.BuynewcarApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.CardResult;
import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.im.dtos.SeriesCityImInfo;
import com.autohome.app.cars.service.components.owner.dtos.SeriesCityYangche;
import com.autohome.app.cars.service.components.owner.dtos.SeriesCityZhaodijiaInfo;
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
 * （李李佳年后出个新接口）
 * 找底价
 * http://buynewcar.api.in.autohome.com.cn/api/series/bidding/v2/portal?_appid=app&cityid=110100&seriesid=66
 */
@Component
@DBConfig(tableName = "series_city_zhaodijia")
public class SeriesCityZhaodijiaComponent extends BaseComponent<SeriesCityZhaodijiaInfo> {

    @Autowired
    BuynewcarApiClient buynewcarApiClient;
    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName,cityId).build();
    }

    public CompletableFuture<SeriesCityZhaodijiaInfo> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {

        loopSeriesCity(totalMinutes,(seriesId,cityId)->{
            buynewcarApiClient.getZhaoyouhui(seriesId,cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }
                if (data.getResult() == null) {
                    delete(makeParam(seriesId,cityId));
                    return;
                }
                SeriesCityZhaodijiaInfo dto = new SeriesCityZhaodijiaInfo();
                dto.setSeriesId(seriesId);
                dto.setCityId(cityId);
                dto.setTitle(data.getResult().getTitle());
                dto.setSubTitle(data.getResult().getSubTitle());
                dto.setIconUrl(data.getResult().getIconUrl());
                dto.setTargetUrl(data.getResult().getTargetUrl());

                update(makeParam(seriesId, cityId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        },xxlLog);
    }
}
