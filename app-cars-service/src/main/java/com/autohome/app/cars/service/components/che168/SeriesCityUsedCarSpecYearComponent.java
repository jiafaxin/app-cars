package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.dtos.GetUsedCarSpecYearList;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarSpecYearDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/5/14
 */
@Component
@DBConfig(tableName = "series_city_usedcar_spec_year")
public class SeriesCityUsedCarSpecYearComponent extends BaseComponent<SeriesCityUsedCarSpecYearDto> {

    @SuppressWarnings("all")
    @Autowired
    Api2scautork2Client api2scautork2Client;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

//    public CompletableFuture<SeriesCityUsedCarSpecYearDto> get(int seriesId, int cityId) {
//        return baseGetAsync(makeParam(seriesId, cityId));
//    }

    public CompletableFuture<SeriesCityUsedCarSpecYearDto> get(int seriesId, int cityId, int year) {
        BaseModel<GetUsedCarSpecYearList> data =
                api2scautork2Client.GetUsedCarSpecYearList(seriesId, cityId, year).join();
        if (data == null || data.getReturncode() != 0) {
            return CompletableFuture.completedFuture(null);
        }

        if (data.getResult() == null
                || data.getResult().getList() == null
                || data.getResult().getList().isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        SeriesCityUsedCarSpecYearDto dto = new SeriesCityUsedCarSpecYearDto();
        dto.setList(new ArrayList<>());
        data.getResult().getList().forEach(item -> {
            SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Spec spec =
                    new SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Spec();
            spec.setSpecid(item.getSpecid());
            spec.setSpecname(item.getSpecname());
            spec.setPrice(item.getPrice());
            spec.setFollow(item.getFollow());
            spec.setDynamicprice(item.getDynamicprice());
            spec.setMoreurl(item.getMoreurl());
            spec.setList(new ArrayList<>());

            if (item.getList() != null && !item.getList().isEmpty()) {
                item.getList().forEach(dealerItem -> {
                    SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Dealer dealer =
                            new SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Dealer();
                    dealer.setInfoid(dealerItem.getInfoid());
                    dealer.setCarname(dealerItem.getCarname());
                    dealer.setPrice(dealerItem.getPrice());
                    dealer.setBrandid(dealerItem.getBrandid());
                    dealer.setSeriesid(dealerItem.getSeriesid());
                    dealer.setSpecid(dealerItem.getSpecid());
                    dealer.setMileage(dealerItem.getMileage());
                    dealer.setRegdate(dealerItem.getRegdate());
                    dealer.setCityname(dealerItem.getCityname());
                    dealer.setCartype(dealerItem.getCartype());
                    dealer.setCarlevel(dealerItem.getCarlevel());
                    dealer.setFromtype(dealerItem.getFromtype());
                    dealer.setImageurl(dealerItem.getImageurl());
                    dealer.setTags(dealerItem.getTags());
                    dealer.setUrl(dealerItem.getUrl());
                    spec.getList().add(dealer);
                });
            }
            dto.getList().add(spec);
        });
        return CompletableFuture.completedFuture(dto);
    }

    // TODO chengjincheng 2024/5/14 待替换接口：二手车年代款车源数据
    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) ->
                api2scautork2Client.GetUsedCarSpecYearList(seriesId, cityId, 2024).thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }

                    if (data.getResult() == null
                            || data.getResult().getList() == null
                            || data.getResult().getList().isEmpty()) {
                        delete(makeParam(seriesId, cityId));
                        return;
                    }

                    SeriesCityUsedCarSpecYearDto dto = new SeriesCityUsedCarSpecYearDto();
                    dto.setList(new ArrayList<>());
                    data.getResult().getList().forEach(item -> {
                        SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Spec spec =
                                new SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Spec();
                        spec.setSpecid(item.getSpecid());
                        spec.setSpecname(item.getSpecname());
                        spec.setPrice(item.getPrice());
                        spec.setFollow(item.getFollow());
                        spec.setDynamicprice(item.getDynamicprice());
                        spec.setMoreurl(item.getMoreurl());
                        spec.setList(new ArrayList<>());

                        if (item.getList() != null && !item.getList().isEmpty()) {
                            item.getList().forEach(dealerItem -> {
                                SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Dealer dealer =
                                        new SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Dealer();
                                dealer.setInfoid(dealerItem.getInfoid());
                                dealer.setCarname(dealerItem.getCarname());
                                dealer.setPrice(dealerItem.getPrice());
                                dealer.setBrandid(dealerItem.getBrandid());
                                dealer.setSeriesid(dealerItem.getSeriesid());
                                dealer.setSpecid(dealerItem.getSpecid());
                                dealer.setMileage(dealerItem.getMileage());
                                dealer.setRegdate(dealerItem.getRegdate());
                                dealer.setCityname(dealerItem.getCityname());
                                dealer.setCartype(dealerItem.getCartype());
                                dealer.setCarlevel(dealerItem.getCarlevel());
                                dealer.setFromtype(dealerItem.getFromtype());
                                dealer.setImageurl(dealerItem.getImageurl());
                                dealer.setTags(dealerItem.getTags());
                                dealer.setUrl(dealerItem.getUrl());
                                spec.getList().add(dealer);
                            });
                        }
                        dto.getList().add(spec);
                    });

                    update(makeParam(seriesId, cityId), dto);
                }).exceptionally(e -> {
                    xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }), xxlLog);
    }
}
