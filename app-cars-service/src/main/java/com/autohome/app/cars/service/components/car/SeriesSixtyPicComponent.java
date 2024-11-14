package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.entities.Car60PictureEntity;
import com.autohome.app.cars.mapper.popauto.entities.CarSixtyPointEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SixtyPicListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
@RedisConfig
@DBConfig(tableName = "series_sixty_pic")
@Slf4j
public class SeriesSixtyPicComponent extends BaseComponent<List<SixtyPicListDto>> {
    static String paramName = "seriesId";

    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;


    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<List<SixtyPicListDto>> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> log) {
        List<CarSixtyPointEntity> carSixtyPointItem = carPhotoViewMapper.getCarSixtyPointItem();
        Map<Integer, String> sixtyPicSortMap = carSixtyPointItem.stream().collect(Collectors.toMap(CarSixtyPointEntity::getOrdercls, CarSixtyPointEntity::getPointname));

        loopSeries(totalMinutes, seriesId -> {
            List<Car60PictureEntity> car60PicList = carPhotoViewMapper.getCar60PicViewBySeries(seriesId);
            List<SixtyPicListDto> sixtyPicListVoList = new ArrayList<>();
            if (car60PicList != null && !car60PicList.isEmpty()) {
                Map<Integer, List<Car60PictureEntity>> mapSortId = car60PicList.stream().collect(groupingBy(Car60PictureEntity::getOrdercls));
                if (!mapSortId.isEmpty()) {
                    for (Map.Entry<Integer, List<Car60PictureEntity>> entry : mapSortId.entrySet()) {
                        SixtyPicListDto vo = new SixtyPicListDto();
                        int sortId = entry.getKey();
                        List<SixtyPicListDto.SpecPic> specPicList = new ArrayList<>();
                        for (Car60PictureEntity po : entry.getValue()) {
                            SixtyPicListDto.SpecPic specPic = new SixtyPicListDto.SpecPic();
                            specPic.setSpecid(po.getSpecId());
                            specPic.setPic(CarSettings.getInstance().GetFullImagePath(po.getPicPath()));
                            specPic.setPicid(po.getPicId());
                            specPicList.add(specPic);
                        }
                        vo.setSixtypicsortid(sortId);
                        if (sixtyPicSortMap.containsKey(sortId)) {
                            vo.setName(sixtyPicSortMap.get(sortId));
                        }
                        vo.setSpecpic(specPicList);
                        sixtyPicListVoList.add(vo);
                    }
                }
            }
            if (sixtyPicListVoList.isEmpty()) {
                delete(makeParam(seriesId));
            } else {
                update(makeParam(seriesId), sixtyPicListVoList);
            }
        }, log);
    }
}
