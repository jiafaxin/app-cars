package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.CpsItem;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecCanAskPriceNewApiResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityCpsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 */
@Component
@DBConfig(tableName = "series_city_cps")
public class SeriesCityCpsComponent extends BaseComponent<SeriesCityCpsDto> {

    @Autowired
    DealerApiClient dealerApiClient;

    final static String seriesIdParamName = "seriesId";

    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityCpsDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        HashSet<String> newKeys = new HashSet<>();
        for (Integer cityId : CityUtil.getAllCityIds()) {
            int pageIndex = 1;
            int pageSize = 50;
            while (pageIndex < 40) {
                long s = System.currentTimeMillis();
                BaseModel<List<CpsItem>> result = dealerApiClient.getCpsProduct(cityId, pageIndex, pageSize).exceptionally(e -> {
                    xxlLog.accept(cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join();
                pageIndex++;
                if (result != null && result.getResult() != null && result.getReturncode() == 0) {
                    if (result.getResult().size() <= 0) {
                        break;
                    }
                    for (CpsItem item : result.getResult()) {
                        SeriesCityCpsDto cityCpsDto = new SeriesCityCpsDto();
                        cityCpsDto.setSeriesId(item.getSeriesId());
                        cityCpsDto.setCityId(cityId);
                        cityCpsDto.setPrice(item.getFacRebateAmount());
                        update(makeParam(item.getSeriesId(), cityId), cityCpsDto);
                        newKeys.add(getKey(makeParam(item.getSeriesId(), cityId)));
                    }
                    if (result.getResult().size() < 45) {
                        xxlLog.accept(cityId + ": 共执行" + pageIndex + "页");
                        break;
                    }
                } else {
                    xxlLog.accept(cityId + ": 共执行" + pageIndex + "页");
                    break;
                }
                long d = System.currentTimeMillis() - s;
                if (d < 70) {
                    ThreadUtil.sleep((int) (70 - d));
                }
            }
        }
        deleteHistory(newKeys,xxlLog);
    }
}
