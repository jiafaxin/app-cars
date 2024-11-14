package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.dtos.GetUsedCarExchangeInfoResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarExchangeDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarSpecYearDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/5/14
 */
@Component
@DBConfig(tableName = "series_city_usedcar_exchange")
public class SeriesCityUsedCarExchangeComponent extends BaseComponent<SeriesCityUsedCarExchangeDto> {

    @SuppressWarnings("all")
    @Autowired
    private Api2scautork2Client api2scautork2Client;

    @Autowired
    private SeriesMapper seriesMapper;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityUsedCarExchangeDto> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        Map<Integer, Integer> seriesBrandMap = allSeries.stream()
                .collect(Collectors.toMap(SeriesEntity::getId, SeriesEntity::getBrandId));

        loopSeriesCity(totalMinutes, (seriesId, cityId) ->
                api2scautork2Client.getUsedCarExchangeInfo(seriesId, cityId, seriesBrandMap.get(seriesId))
                        .thenAccept(data -> {
                            if (data == null || data.getReturncode() != 0) {
                                return;
                            }

                            if (data.getResult() == null
                                    || data.getResult().getDatasell() == null) {
                                delete(makeParam(seriesId, cityId));
                                return;
                            }

                            SeriesCityUsedCarExchangeDto dto = new SeriesCityUsedCarExchangeDto();
                            SeriesCityUsedCarExchangeDto.S2scExchangeResult_Data exchangeData =
                                    new SeriesCityUsedCarExchangeDto.S2scExchangeResult_Data();
                            exchangeData.setIconurl(data.getResult().getDatasell().getIconurl());
                            exchangeData.setUrl(data.getResult().getDatasell().getUrl());
                            exchangeData.setMaintitle(data.getResult().getDatasell().getMaintitle());
                            exchangeData.setSubhead(data.getResult().getDatasell().getSubhead());
                            dto.setDatasell(exchangeData);

                            update(makeParam(seriesId, cityId), dto);
                        }).exceptionally(e -> {
                            xxlLog.accept(seriesId + "-" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                            return null;
                        }), xxlLog);
    }
}