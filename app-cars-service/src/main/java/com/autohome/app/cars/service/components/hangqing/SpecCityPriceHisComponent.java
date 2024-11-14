package com.autohome.app.cars.service.components.hangqing;

import com.alibaba.fastjson2.JSONArray;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecCityPriceHisResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.mapper.appcars.SpecCityPriceHistoryMapper;
import com.autohome.app.cars.mapper.appcars.entities.SpecCityPriceHistoryEntity;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHisDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHistoryDto;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/7/29
 */
@Slf4j
@Component
@DBConfig(tableName = "spec_city_price_his")
public class SpecCityPriceHisComponent extends BaseComponent<List<SpecCityPriceHisDto>> {
    final static String specIdParamName = "specId";

    final static String cityParamName = "cityId";


    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesSpecComponent seriesSpecComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private DealerApiClient dealerApiClient;

    @Autowired
    private SpecCityPriceHistoryMapper specCityPriceHistoryMapper;

    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return ParamBuilder.create(specIdParamName, specId).add(cityParamName, cityId).build();
    }


    public List<SpecCityPriceHisDto> get(int specId, int cityId) {
        return baseGet(makeParam(specId, cityId));
    }

    public Map<Integer, Map<Integer, List<SpecCityPriceHisDto>>> getSpecCityPriceMap(List<Integer> specIdList) {
        Map<Integer, Map<Integer, List<SpecCityPriceHisDto>>> resultMap = new HashMap<>();
        List<String> list = specIdList.stream().map(Object::toString).toList();
        String specIds = String.join(",", list);
        List<SpecCityPriceHistoryEntity> specCityPriceHistoryEntities = specCityPriceHistoryMapper.selectBySpecIdIn(specIds);
        Map<Integer, List<SpecCityPriceHistoryEntity>> collect = specCityPriceHistoryEntities.stream().collect(Collectors.groupingBy(SpecCityPriceHistoryEntity::getCityId));

        collect.forEach((k, dtoList)->{
            Map<Integer, List<SpecCityPriceHisDto>> specMap = dtoList.stream().collect(Collectors.toMap(SpecCityPriceHistoryEntity::getSpecId, x -> JSONArray.parseArray(x.getData(), SpecCityPriceHisDto.class)));
            resultMap.put(k, specMap);
        });

        return resultMap;
    }


    public void refreshAllCity(Consumer<String> xxlLog) {
        CityUtil.getAllCityIds().forEach(cityId -> {
            try {
                handle(cityId, xxlLog);
                xxlLog.accept(String.format("城市%s%s", CityUtil.getCityName(cityId), cityId) + "数据更新成功");
            } catch (Exception e) {
                xxlLog.accept(String.format("城市%s%s", CityUtil.getCityName(cityId), cityId) + "数据更新失败"
                        + Arrays.toString(e.getStackTrace()));
            }
        });
    }

    public void refreshAllCityTest(int cityId, Consumer<String> xxlLog) {
        handle(cityId, xxlLog);
    }

    private void handle(int cityId, Consumer<String> xxlLog) {
        // 对应城市有经销商报价的车系id
        List<Integer> seriesIdList = getSeriesIdListByCity(cityId);
        List<SpecPriceDto> errorSpecList = new ArrayList<>();
        seriesIdList.forEach(seriesId -> {
            List<SpecPriceDto> specPriceDtoList = getSpecPriceDtoList(cityId, seriesId);
            if (!CollectionUtils.isEmpty(specPriceDtoList)) {
                // 对每一个spec进行处理
                specPriceDtoList.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getDealerMinPrice() > 0)
                        .forEach(specPriceDto -> handleSpec(cityId, specPriceDto, xxlLog, errorSpecList));
            }
        });
        // 异常数据加一层重跑
        errorSpecList.forEach(errorSpec -> handleSpec(cityId, errorSpec, xxlLog, new ArrayList<>()));
    }

    private void handleSpec(int cityId,
                            SpecPriceDto specPriceDto,
                            Consumer<String> xxlLog,
                            List<SpecPriceDto> errorSpecList) {
        try {
            BaseModel<List<SpecCityPriceHisResult>> result =
                    dealerApiClient.getDealerListPriceHis(cityId, specPriceDto.getSpecId()).join();
            if (Objects.nonNull(result)
                    && Objects.nonNull(result.getResult())
                    && !CollectionUtils.isEmpty(result.getResult())
                    && result.getResult().size() >= 2) {
                if (result.getResult().get(result.getResult().size() - 1).getNewsPrice() == null) {
                    // 凌晨查询数据，T-1日为空，把T-2的数据赋值给T-1，便于后续计算
                    result.getResult().get(result.getResult().size() - 1).setNewsPrice(
                            result.getResult().get(result.getResult().size() - 2).getNewsPrice());
                }
                List<SpecCityPriceHisDto> resultList = new ArrayList<>(
                        result.getResult().stream().map(specPriceHis -> {
                            SpecCityPriceHisDto specCityPriceHisDto = new SpecCityPriceHisDto();
                            specCityPriceHisDto.setDate(specPriceHis.getDate());
                            specCityPriceHisDto.setNewsPrice(Objects.isNull(specPriceHis.getNewsPrice())
                                    ? 0
                                    : specPriceHis.getNewsPrice());
                            return specCityPriceHisDto;
                        }).toList());
                String todayStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
                if (!todayStr.equals(resultList.get(resultList.size() - 1).getDate())) {
                    // 最后一天数据不是今日，把今日数据加到最后
                    SpecCityPriceHisDto specCityPriceHisDto = new SpecCityPriceHisDto();
                    specCityPriceHisDto.setDate(todayStr);
                    specCityPriceHisDto.setNewsPrice(specPriceDto.getDealerMinPrice());
                    resultList.add(specCityPriceHisDto);
                } else {
                    // 若今日已有数据，则更新数据
                    resultList.get(resultList.size() - 1).setNewsPrice(specPriceDto.getDealerMinPrice());
                }
                update(makeParam(specPriceDto.getSpecId(), cityId), resultList);
                // 没有值就不更新，目前看不用删除逻辑
            } else {
                errorSpecList.add(specPriceDto);
                log.warn("数据获取失败， spec={}", specPriceDto.getSpecId());
            }
        } catch (Exception e) {
            errorSpecList.add(specPriceDto);
            log.warn("数据获取异常， spec={}", specPriceDto.getSpecId(), e);
        }
    }

    /**
     * 获取待处理车系
     *
     * @param cityId
     * @return
     */
    private List<Integer> getSeriesIdListByCity(int cityId) {
        List<Integer> resultSeriesIdList = new ArrayList<>();
        // 对应城市有经销商报价的车系id
        List<Integer> seriesIdList = seriesCityAskPriceNewComponent.getSeriesByCity(cityId);
        // 商用车levelId
        List<Integer> commercialSeriesLevelIds = Arrays.asList(11, 12, 13, 14, 25);
        List<List<Integer>> seriesIdListPart = Lists.partition(seriesIdList, 100);
        // 过滤出乘用车和在售车系
        seriesIdListPart.forEach(seriesIdPart -> {
            List<SeriesDetailDto> seriesDetailDtoList = seriesDetailComponent.getList(seriesIdPart)
                    .thenCombineAsync(seriesCityAskPriceNewComponent.get(seriesIdPart, cityId), (seriesDetailDtos, seriesCityAskPrices) -> {
                        Map<Integer, SeriesCityAskPriceDto> seriesCityAskPriceDtoMap = seriesCityAskPrices.stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(SeriesCityAskPriceDto::getSeriesId, Function.identity(),
                                        (k1, k2) -> k2));
                        return seriesDetailDtos.stream()
                                .filter(Objects::nonNull)
                                .filter(e -> !commercialSeriesLevelIds.contains(e.getLevelId())
                                        && Arrays.asList(20, 30).contains(e.getState()))
                                .filter(e -> Objects.nonNull(seriesCityAskPriceDtoMap.get(e.getId()))
                                        && seriesCityAskPriceDtoMap.get(e.getId()).isLocalPrice(cityId))
                                .toList();
                    }, ThreadPoolUtils.defaultThreadPoolExecutor).join();
            if (!CollectionUtils.isEmpty(seriesDetailDtoList)) {
                resultSeriesIdList.addAll(seriesDetailDtoList.stream().map(SeriesDetailDto::getId).toList());
            }
        });
        return resultSeriesIdList;
    }

    /**
     * 获取报价信息
     *
     * @param cityId
     * @param seriesId
     * @return
     */
    private List<SpecPriceDto> getSpecPriceDtoList(int cityId, int seriesId) {
        SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesId);
        if (Objects.isNull(seriesSpecDto)
                || CollectionUtils.isEmpty(seriesSpecDto.getItems())) {
            return Collections.emptyList();
        }
        List<SeriesSpecDto.Item> specPriceDtoList = seriesSpecDto.getItems().stream()
                .filter(e -> e.getState() == 20 || e.getState() == 30)
                .toList();
        Map<Integer, Integer> specMinPriceMap = specPriceDtoList.stream()
                .collect(Collectors.toMap(SeriesSpecDto.Item::getId, SeriesSpecDto.Item::getMinPrice, (k1, k2) -> k2));
        List<Integer> onSaleSpecIdList = specPriceDtoList.stream()
                .map(SeriesSpecDto.Item::getId)
                .toList();
        return specCityAskPriceComponent.getListAsync(onSaleSpecIdList, cityId).thenApply(specCityAskPriceDtoList ->
                specCityAskPriceDtoList.stream()
                        .filter(Objects::nonNull) // 必须有报价
                        .filter(e -> e.getMinPriceCityId() == cityId
                                || (!CollectionUtils.isEmpty(e.getMinPriceCityIdList())
                                && e.getMinPriceCityIdList().contains(cityId))) // 最低价是当前城市，询价不降维
                        .map(e -> {
                            SpecPriceDto specPriceDto = new SpecPriceDto();
                            specPriceDto.setSpecId(e.getSpecId());
                            specPriceDto.setSpecMinPrice(specMinPriceMap.get(e.getSpecId()));
                            specPriceDto.setDealerMinPrice(e.getMinPrice());
                            return specPriceDto;
                        })
                        .toList()).join();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpecPriceDto {

        private int specId;

        private int specMinPrice;

        private int dealerMinPrice;
    }


    @Deprecated
    public void updateTemp(int specId, int cityId, List<SpecCityPriceHisDto> dtoList) {
        if (!CollectionUtils.isEmpty(dtoList)) {
            update(makeParam(specId, cityId), dtoList);
        }
    }
}
