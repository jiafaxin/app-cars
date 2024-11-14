package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.PlayCarCardResult;
import com.autohome.app.cars.apiclient.owner.dtos.UseCarCardResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.services.enums.TypeIdEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车系用车Tab
 */
@Component
@DBConfig(tableName = "series_use_car")
public class SeriesUseCarComponent extends BaseComponent<UseCarCardResult> {

    static String paramName = "seriesId";

    final static String cityParamName = "cityId";

    @Autowired
    OwnerApiClient ownerApiClient;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(paramName, seriesId).add(cityParamName, cityId).build();
    }

    //启用压缩
    @Override
    protected boolean gzip() {
        return true;
    }

    public CompletableFuture<UseCarCardResult> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshClearAll(Consumer<String> xxlLog) {
        loopSeriesCity(60, (seriesId, cityId) -> {
            delete(makeParam(seriesId, cityId));
        }, xxlLog);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            refreshOne(xxlLog, seriesId, cityId);
        }, xxlLog);

//        loopSeries(totalMinutes, seriesId -> {
//            refreshOne(xxlLog, seriesId, 110100);
//        }, xxlLog);
    }

    public void refreshOne(Consumer<String> xxlLog, int seriesId, int cityId) {
        int provinceId = cityId / 1000 * 1000;
        ownerApiClient.getUseCarCard(seriesId, provinceId, cityId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }
            if (data.getResult() == null) {
                delete(makeParam(seriesId, cityId));
                return;
            }
            UseCarCardResult dto = data.getResult();

            dto.getTopConfigItems().forEach(item -> {
                item.setImgUrl(ImageUtils.convertImageUrl(item.getImgUrl(), true, false, false));
                item.setImgUrlForRN(ImageUtils.convertImageUrl(item.getImgUrlForRN(), true, false, false));
                TypeIdEnum typeIdEnum = TypeIdEnum.getByCode(item.getCode());
                if (typeIdEnum != null) {
                    item.setTypeid(typeIdEnum.getTypeid());
                }
            });
            dto.getCards().forEach(item -> {
                item.setImgUrl(ImageUtils.convertImageUrl(item.getImgUrl(), true, false, false));
                TypeIdEnum typeIdEnum = TypeIdEnum.getByCode(item.getCode());
                if (typeIdEnum != null) {
                    item.setTypeid(typeIdEnum.getTypeid());
                }
            });

            update(makeParam(seriesId, cityId), dto);
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        });
    }
}
