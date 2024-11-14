package com.autohome.app.cars.service.components.misc;

import com.autohome.app.cars.apiclient.opscard.OpsCardApiClient;
import com.autohome.app.cars.apiclient.opscard.dtos.SeriesHotNewsResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.misc.dtos.SeriesCityHotNewsDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/7/19
 */
@Component
@DBConfig(tableName = "series_city_hot_news")
public class SeriesCityHotNewsComponent extends BaseComponent<List<SeriesCityHotNewsDto>> {

    @SuppressWarnings("all")
    @Autowired
    OpsCardApiClient opsCardApiClient;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";


    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<List<SeriesCityHotNewsDto>> getAsync(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> logInfo) {
        // 本次新计算得到的key，用于删除历史数据
        ConcurrentHashSet<String> newKeys = new ConcurrentHashSet<>();
        // 异常标志
        AtomicBoolean exFlag = new AtomicBoolean(false);

        loopCity(totalMinutes, cityId -> {
            BaseModel<SeriesHotNewsResult> resultBaseModel = opsCardApiClient.getSeriesHotNews(cityId).join();
            if (Objects.nonNull(resultBaseModel)
                    && Objects.nonNull(resultBaseModel.getResult())
                    && !CollectionUtils.isEmpty(resultBaseModel.getResult().getCards())) {
                try {
                    SeriesHotNewsResult.Card hotNewsCard = resultBaseModel.getResult().getCards().stream()
                            .filter(e -> StringUtils.equals(e.getCardtag(), "series_hot_news"))
                            .findFirst().orElse(null);
                    if (Objects.nonNull(hotNewsCard)
                            && !CollectionUtils.isEmpty(hotNewsCard.getCells())) {
                        List<SeriesCityHotNewsDto> dtoList = new ArrayList<>();
                        List<SeriesHotNewsResult.Card.Cell> cellList = hotNewsCard.getCells();
                        for (SeriesHotNewsResult.Card.Cell cell : cellList) {
                            List<Integer> seriesIdList = Arrays.stream(cell.getSeriesids().split(","))
                                    .map(Integer::valueOf).toList();
                            seriesIdList.forEach(seriesId -> {
                                SeriesCityHotNewsDto dto = new SeriesCityHotNewsDto();
                                dto.setSeriesId(seriesId);
                                dto.setIcon(cell.getIcon());
                                dto.setPageCardDataId(cell.getPagecarddataid());
                                dto.setLinkUrl(cell.getLinkurl());
                                dto.setSort(cell.getSort());
                                dto.setPosition(cell.getPosition());
                                dto.setTitle(cell.getTitle());
                                dtoList.add(dto);
                            });
                        }
                        Map<Integer, List<SeriesCityHotNewsDto>> seriesHotNewsMap = dtoList.stream()
                                .collect(Collectors.groupingBy(SeriesCityHotNewsDto::getSeriesId));
                        seriesHotNewsMap.forEach((k, v) -> update(makeParam(k, cityId), v));
                        logInfo.accept(String.format("数据更新成功，cityId=%s", cityId));
                        newKeys.addAll(seriesHotNewsMap.keySet().stream()
                                .map(k -> makeParam(k, cityId))
                                .map(this::getKey)
                                .collect(Collectors.toSet()));
                    }
                } catch (Exception e) {
                    exFlag.set(true);
                    logInfo.accept(String.format("数据更新异常，cityId=%s", cityId));
                }
            }
        }, logInfo);

        if (!exFlag.get()) {
            logInfo.accept("SpecCitySubsidyComponent 数据同步任务出现异常，不执行历史数据删除任务");
            deleteHistorys(new HashSet<>(newKeys), logInfo);
        }
    }

    public void deleteHistorys(HashSet<String> newKeys, Consumer<String> xxlLog) {
        deleteHistory(newKeys, xxlLog);
    }
}
