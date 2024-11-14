package com.autohome.app.cars.service.components.newretail;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.apiclient.newretail.AhohClient;
import com.autohome.app.cars.apiclient.newretail.dtos.StoreSeriesItem;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.components.newretail.dtos.CitySeriesListDto;
import com.autohome.app.cars.service.services.dtos.TestDriveConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 和付静确认走下面这个接口，提测的时候，拉付静一起和测试说
 * http://koubei.api.sjz.autohome.com.cn/api/series/carbean?_appid=app&seriesid=18 (result.scheme)
 */
@Component
@DBConfig(tableName = "newretail_city_series")
public class NewRetailCitySeriesComponent extends BaseComponent<CitySeriesListDto> {

    static String paramName = "cityId";

    @Autowired
    AhohClient ahohClient;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.TestDriveConfig).createFromJson('${testdrive_config:}')}")
    private TestDriveConfig testDriveConfig;

    TreeMap<String, Object> makeParam(int cityId) {
        return ParamBuilder.create(paramName, cityId).build();
    }


    public CompletableFuture<CitySeriesListDto.Series> get(int seriesId,int cityId,int noDefaultCityId) {
        if(!CityUtil.isDefaultCity(cityId, noDefaultCityId) && testDriveConfig != null && testDriveConfig.getIsOpen() == 1 && testDriveConfig.getSeriesCityList().contains(cityId)){
            return getBaseData(seriesId,cityId);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<CitySeriesListDto.Series> getB(int seriesId,int cityId,int noDefaultCityId) {
        if(!CityUtil.isDefaultCity(cityId, noDefaultCityId) && testDriveConfig != null && testDriveConfig.getCityList().contains(cityId)){
            return getBaseData(seriesId,cityId);
        }
        return CompletableFuture.completedFuture(null);
    }

     CompletableFuture<CitySeriesListDto.Series> getBaseData(int seriesId, int cityId) {
        return baseGetAsync(makeParam(cityId)).thenApply(list -> {
            if (list == null || list.getItems() == null || list.getItems().isEmpty())
                return null;
            return list.getItems().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
        });
    }
    public void refreshAll(Consumer<String> xxlLog) {
        loopCity(8, cityId -> {
            ahohClient.getStoreSeriesList(cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null || data.getResult().size() == 0) {
                    delete(makeParam(cityId));
                    return;
                }
                CitySeriesListDto dto = new CitySeriesListDto();
                dto.setCityId(cityId);
                for (StoreSeriesItem storeSeriesItem : data.getResult()) {
                    dto.getItems().add(new CitySeriesListDto.Series() {{
                        setSeriesId(storeSeriesItem.getSeriesId());
                    }});
                }
                update(makeParam(cityId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        }, xxlLog);
    }

}
