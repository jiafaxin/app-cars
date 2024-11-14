package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecCanAskPriceNewApiResult;
import com.autohome.app.cars.apiclient.dealer.dtos.ListCshDealerByCityResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecPriceItem;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 */
@Component
@DBConfig(tableName = "series_city_askprice")
public class SeriesCityAskPriceComponent extends BaseComponent<SeriesCityAskPriceDto> {

    @Autowired
    DealerApiClient dealerApiClient;

    @Autowired
    SpecMapper specMapper;

    @Autowired
    SpecCityAskPriceComponent specCityAskPriceComponent;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityAskPriceDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            dealerApiClient.getSeriesMinpriceWithSpecs(seriesId, cityId).thenAccept(data -> {
                processData(seriesId, cityId, data);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }

    @Override
    protected SeriesCityAskPriceDto sourceData(TreeMap<String, Object> params) {
        Integer seriesId = (Integer) params.get(seriesIdParamName);
        Integer cityId = (Integer) params.get(cityParamName);

        return processData(seriesId, cityId, dealerApiClient.getSeriesMinpriceWithSpecs(seriesId, cityId).join());
    }

    public SeriesCityAskPriceDto test4(int seriesId,int cityId) {
        return processData(seriesId, cityId, dealerApiClient.getSeriesMinpriceWithSpecs(seriesId, cityId).join());
    }

    private SeriesCityAskPriceDto processData(Integer seriesId, Integer cityId, BaseModel<List<DealerSpecCanAskPriceNewApiResult>> data) {
        if (data == null || data.getReturncode() != 0) {
            return null;
        }

        if (data.getResult() == null || data.getResult().size() == 0 || data.getResult().get(0) == null) {
            delete(makeParam(seriesId, cityId));
            return null;
        }

        int minPrice = 0;
        int maxPrice = 0;

        DealerSpecCanAskPriceNewApiResult item = data.getResult().get(0);
        SeriesCityAskPriceDto dto = new SeriesCityAskPriceDto();
        if (item.getSpecs() != null && item.getSpecs().size() > 0) {
            minPrice = item.getSpecs().stream().filter(x -> x.getSpecState() != 40 && x.getNewsPrice() > 0).mapToInt(x -> x.getNewsPrice()).min().orElse(0);
            maxPrice = item.getSpecs().stream().filter(x -> x.getSpecState() != 40 && x.getNewsPrice() > 0).mapToInt(x -> x.getNewsPrice()).max().orElse(0);
            dto.setSpecCount(item.getSpecs().size());
        }

        dto.setMinPrice(minPrice);
        dto.setMaxPrice(maxPrice);

        update(makeParam(seriesId, cityId), dto);

        return dto;
    }



}
