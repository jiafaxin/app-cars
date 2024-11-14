package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityHotUsedCarDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesUsedCarInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车系车型列表-热门二手车
 */
@Component
@DBConfig(tableName = "series_city_hot_usedcar")
public class SeriesCityHotUsedCarComponent extends BaseComponent<SeriesCityHotUsedCarDto> {

    @Autowired
    Api2scautork2Client api2scautork2Client;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";


    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityHotUsedCarDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            api2scautork2Client.GetSeriesHotSpecs(seriesId, cityId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null || data.getResult().getList() == null || data.getResult().getList().isEmpty()) {
                    delete(makeParam(seriesId, cityId));
                    return;
                }

                SeriesCityHotUsedCarDto dto = new SeriesCityHotUsedCarDto();
                data.getResult().getList().forEach(item -> {
                    SeriesCityHotUsedCarDto.HotSpecItem hotSpecItem = new SeriesCityHotUsedCarDto.HotSpecItem();
                    hotSpecItem.setSpecid(item.getSpecid());
                    hotSpecItem.setSpecname(item.getSpecname());
                    hotSpecItem.setPrice(item.getPrice());
                    hotSpecItem.setFollow(item.getFollow());
                    hotSpecItem.setDynamicprice(item.getDynamicprice());
                    hotSpecItem.setMoreurl(item.getMoreurl());

                    if (item.getList() != null && !item.getList().isEmpty()) {
                        item.getList().forEach(dealerItem -> {
                            SeriesCityHotUsedCarDto.HotSpecDealerItem hotSpecDealerItem = new SeriesCityHotUsedCarDto.HotSpecDealerItem();
                            hotSpecDealerItem.setCarname(dealerItem.getCarname());
                            hotSpecDealerItem.setPrice(dealerItem.getPrice());
                            hotSpecDealerItem.setSpecid(dealerItem.getSpecid());
                            hotSpecDealerItem.setMileage(dealerItem.getMileage());
                            hotSpecDealerItem.setRegdate(dealerItem.getRegdate());
                            hotSpecDealerItem.setCityname(dealerItem.getCityname());
                            hotSpecDealerItem.setImageurl(dealerItem.getImageurl());
                            hotSpecDealerItem.setUrl(dealerItem.getUrl());
                            hotSpecItem.getList().add(hotSpecDealerItem);
                        });
                    }
                    dto.getList().add(hotSpecItem);
                });

                update(makeParam(seriesId, cityId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }
}
