package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.carconfig.Level;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.BrightpointsconfigMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecHighlightMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrightPointConfigEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesBrightpointDto;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系亮点
 */
@Component
@DBConfig(tableName = "series_brightpoint")
public class SeriesBrightpointComponent extends BaseComponent<SeriesBrightpointDto> {

    @Autowired
    SpecMapper specMapper;

    @Autowired
    SpecHighlightMapper specHighlightMapper;
    @Autowired
    SeriesMapper seriesMapper;
    @Autowired
    BrightpointsconfigMapper brightpointsconfigMapper;

    static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesBrightpointDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<BrightPointConfigEntity> allSeriesBrightPointConfigs = brightpointsconfigMapper.getAllSeriesBrightPointConfigs();
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        allSeries.forEach(series -> {
            //品库亮点
            SeriesBrightpointDto.CarSeriesHighlight carSeriesHighlight = null;
            //魔方亮点
            SeriesBrightpointDto.Brightpoint brightpoint = null;
            if (allSeriesBrightPointConfigs != null) {
                Optional<BrightPointConfigEntity> first = allSeriesBrightPointConfigs.stream().filter(x -> x.getSeriesid() == series.getId()).findFirst();
                if (first.isPresent()) {
                    brightpoint = new SeriesBrightpointDto.Brightpoint();
                    brightpoint.setUrl(first.get().getUrl());
                    brightpoint.setTitle(first.get().getTitle());
                }
            }
            if (series.getState() == 20 || series.getState() == 30) {
                List<SpecEntity> specEntityList = Level.isCVLevel(series.getLevelId()) ? specMapper.getCvSpecBySeriesId(series.getId()) : specMapper.getSpecBySeriesId(series.getId());
                if (CollectionUtils.isNotEmpty(specEntityList)) {
                    Optional<SpecEntity> maxPriceEntityOptional = specEntityList.stream()
                            .filter(specEntity -> specEntity.getState() == 20 || specEntity.getState() == 30)// 筛选出state=20或30的数据
                            .max(Comparator.comparing(SpecEntity::getMaxPrice));
                    int sum =0;int specId=0;
                    if (maxPriceEntityOptional.isPresent()) {
                        specId = maxPriceEntityOptional.get().getId();
                        Integer a = specHighlightMapper.getSpecStandardConfigAndRelationPicList(specId);
                        Integer b = specHighlightMapper.getSpecParamListOfHighLight(specId, maxPriceEntityOptional.get().getFuelType());
                        sum = (a == null ? 0 : a) + (b == null ? 0 : b);
                    }

                    List<Integer> minPriceSpecIds = getMinPriceSpecIds(specEntityList);
                    if (sum > 0 || !minPriceSpecIds.isEmpty()) {
                        carSeriesHighlight = new SeriesBrightpointDto.CarSeriesHighlight();
                        carSeriesHighlight.setSeriesId(series.getId());
                        carSeriesHighlight.setHighlightCount(sum);
                        carSeriesHighlight.setSpecId(specId);
                        carSeriesHighlight.setMinPriceSpecIds(minPriceSpecIds);
                    }
                }
            }

            if (brightpoint != null || carSeriesHighlight != null) {
                SeriesBrightpointDto brightpointDto = new SeriesBrightpointDto();
                brightpointDto.setSeriesId(series.getId());
                brightpointDto.setBrightpoint(brightpoint);
                brightpointDto.setCarSeriesHighlight(carSeriesHighlight);
                update(makeParam(series.getId()), brightpointDto);
                xxlLog.accept("车系id:" + series.getId() + ", success");
            } else {
                delete(makeParam(series.getId()));
                xxlLog.accept("车系id:" + series.getId() + ", delete");
            }
            ThreadUtil.sleep(50);
        });
    }

    //在售车型，指导价最低的两车型id
    List<Integer> getMinPriceSpecIds(List<SpecEntity> specAll) {
        List<SpecEntity> onsellSpecList = specAll.stream().filter(p -> p.getParamIsShow() == 1 && (p.getState() == 20 || p.getState() == 30)).collect(Collectors.toList());
        if (!onsellSpecList.isEmpty()) {
            Map<Integer, List<SpecEntity>> onsellSpecMap = onsellSpecList.stream().collect(Collectors.groupingBy(SpecEntity::getMinPrice));
            List<Integer> priceList = onsellSpecMap.keySet().stream().collect(Collectors.toList());
            Collections.sort(priceList);
            if (CollectionUtils.isNotEmpty(priceList) && priceList.size() >= 2) {
                int sid1 = onsellSpecMap.get(priceList.get(0)).get(0).getId();
                int sid2 = onsellSpecMap.get(priceList.get(1)).get(0).getId();
                return Arrays.asList(sid2, sid1);
            } else if (CollectionUtils.isNotEmpty(priceList) && priceList.size() == 1) {
                if (onsellSpecMap.get(priceList.get(0)).size() > 1) {
                    int sid1 = onsellSpecMap.get(priceList.get(0)).get(0).getId();
                    int sid2 = onsellSpecMap.get(priceList.get(0)).get(1).getId();
                    return Arrays.asList(sid2, sid1);
                }
            }
        }
        return new ArrayList<>();
    }

}
