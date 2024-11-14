package com.autohome.app.cars.service.components.vr;

import com.autohome.app.cars.common.carconfig.SixtyPic.SixPicToVr;
import com.autohome.app.cars.common.carconfig.SixtyPic.SortIdMapPicClassId;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarSixtyPointEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVrPointDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系vr点位
 */
@Component
@DBConfig(tableName = "series_vr_point")
public class SeriesVrPointComponent extends BaseComponent<List<SeriesVrPointDto>> {

    static String paramName = "seriesId";

    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;


    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<List<SeriesVrPointDto>> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> log) {
        List<CarSixtyPointEntity> carSixtyPointItem = carPhotoViewMapper.getCarSixtyPointItem();
        Map<Integer, String> sixtyPicSortMap = carSixtyPointItem.stream().collect(Collectors.toMap(CarSixtyPointEntity::getOrdercls, CarSixtyPointEntity::getPointname));

        loopSeries(totalMinutes, seriesId -> {
            List<Integer> delegateSpecSixtyPicSortIdList = carPhotoViewMapper.getDeletgateSixtyPicSpecPointLocation(seriesId);

            List<SeriesVrPointDto> seriesSixtyPicMappingVrLocationList = new ArrayList<>();
            if (delegateSpecSixtyPicSortIdList != null && !delegateSpecSixtyPicSortIdList.isEmpty()) {
                //写死的60图对应vr点位关系
                Map<Integer, Integer> mapSixPictoVrPointLocation = SixPicToVr.getMapSixPicVrPointLocation();
                for (int i = 0; i < delegateSpecSixtyPicSortIdList.size(); i++) {
                    SeriesVrPointDto returnObj = new SeriesVrPointDto();
                    Integer sortId = Integer.parseInt(delegateSpecSixtyPicSortIdList.get(i).toString());
                    returnObj.setClassid(sortId);
                    returnObj.setName(sixtyPicSortMap.getOrDefault(sortId, ""));
                    returnObj.setFrameids(mapSixPictoVrPointLocation.containsKey(sortId) ? mapSixPictoVrPointLocation.get(sortId).toString() : "");
                    returnObj.setPicclassid(SortIdMapPicClassId.getPicClassId(sortId));
                    seriesSixtyPicMappingVrLocationList.add(returnObj);
                }
            }
            if (seriesSixtyPicMappingVrLocationList.isEmpty()) {
                delete(makeParam(seriesId));
            } else {
                //按照classid排序
                seriesSixtyPicMappingVrLocationList.sort(Comparator.comparingInt(SeriesVrPointDto::getClassid));
                update(makeParam(seriesId), seriesSixtyPicMappingVrLocationList);
            }
        }, log);
    }
}
