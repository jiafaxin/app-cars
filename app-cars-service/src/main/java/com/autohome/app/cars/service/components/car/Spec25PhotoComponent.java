package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.testdata.TestDataApiClient;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.Car25PhotoEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.CarPhotoDto;
import com.autohome.app.cars.service.components.car.dtos.Spec25PhotoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@DBConfig(tableName = "spec_25_photo")
public class Spec25PhotoComponent extends BaseComponent<List<Spec25PhotoDto>> {
    final static String specIdParamName = "specId";

    @Autowired
    SpecMapper specMapper;
    @Autowired
    TestDataApiClient testDataApiClient;

    @Autowired
    private CarPhotoComponent carPhotoComponent;

    private static final List<Integer> CLASS_IDS = Arrays.asList(1, 3, 10, 12);

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<List<Spec25PhotoDto>> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<Car25PhotoEntity> allSpec25Photos = specMapper.getAllSpec25Photos();
        Map<Integer, List<Car25PhotoEntity>> specPhotosMap = allSpec25Photos.stream().collect(Collectors.groupingBy(Car25PhotoEntity::getSpecId));

        specPhotosMap.forEach((key, value) -> {
            if (!value.isEmpty()) {
                List<Spec25PhotoDto> spec25PhotoDtos = value.stream().map(this::toSpec25PhotoDto).toList();
                update(makeParam(key), spec25PhotoDtos);
                ThreadUtil.sleep(5);
                xxlLog.accept("refresh spec 25 photos: " + key);
            }
        });
    }

    public void refreshAllNew(Consumer<String> xxlLog) {

        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());
        Map<Integer, List<SpecEntity>> seriesMap = specAll.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        //获取所有的25图
        List<Car25PhotoEntity> allSpec25Photos = specMapper.getAllSpec25Photos();
        Map<Integer, List<Car25PhotoEntity>> specPhotosMap = allSpec25Photos.stream().collect(Collectors.groupingBy(Car25PhotoEntity::getSpecId));
        Set<Integer> seriesIds = new HashSet<>(seriesMap.keySet());
        for (Integer seriesId : seriesIds) {
            List<CarPhotoDto> carPhotoDto = carPhotoComponent.getCarPhotoBySeriesId(seriesId).join();
            if (CollectionUtils.isEmpty(carPhotoDto)) {
                continue;
            }
            List<CarPhotoDto> series_data_1 = carPhotoComponent.getData(carPhotoDto, 0, List.of(1));
            List<CarPhotoDto> series_data_2 = carPhotoComponent.getData(carPhotoDto, 0, List.of(3));
            List<CarPhotoDto> series_data_3 = carPhotoComponent.getData(carPhotoDto, 0, List.of(10));
            List<CarPhotoDto> series_data_4 = carPhotoComponent.getData(carPhotoDto, 0, List.of(12));
            List<SpecEntity> specEntities = seriesMap.get(seriesId);
            //遍历当前车系下的所有车型
            for (SpecEntity specEntity : specEntities) {
                int specId = specEntity.getId();
                List<Car25PhotoEntity> car25PhotoEntities = specPhotosMap.get(specId);
                if (!CollectionUtils.isEmpty(car25PhotoEntities)) {
                    int specWgIndex = 0;
                    int specZyIndex = 0;
                    int specZkIndex = 0;
                    int specXjIndex = 0;

                    int seriesWgIndex = 0;
                    int seriesZyIndex = 0;
                    int seriesZkIndex = 0;
                    int seriesXjIndex = 0;
                    List<CarPhotoDto> specId_data_1 = carPhotoComponent.getData(carPhotoDto, specId, List.of(1));
                    List<CarPhotoDto> specId_data_2 = carPhotoComponent.getData(carPhotoDto, specId, List.of(3));
                    List<CarPhotoDto> specId_data_3 = carPhotoComponent.getData(carPhotoDto, specId, List.of(10));
                    List<CarPhotoDto> specId_data_4 = carPhotoComponent.getData(carPhotoDto, specId, List.of(12));
                    Map<Integer, Car25PhotoEntity> picImageMap = car25PhotoEntities.stream().collect(Collectors.toMap(Car25PhotoEntity::getPicId, e -> e));
                    seriesWgIndex = getIndex(seriesWgIndex, series_data_1, picImageMap, CLASS_IDS.get(0), false);
                    seriesZyIndex = getIndex(seriesZyIndex, series_data_2, picImageMap, CLASS_IDS.get(1), false);
                    seriesZkIndex = getIndex(seriesZkIndex, series_data_3, picImageMap, CLASS_IDS.get(2), false);
                    seriesXjIndex = getIndex(seriesXjIndex, series_data_4, picImageMap, CLASS_IDS.get(3), false);

                    specWgIndex = getIndex(specWgIndex, specId_data_1, picImageMap, CLASS_IDS.get(0), true);
                    specZyIndex = getIndex(specZyIndex, specId_data_2, picImageMap, CLASS_IDS.get(1), true);
                    specZkIndex = getIndex(specZkIndex, specId_data_3, picImageMap, CLASS_IDS.get(2), true);
                    specXjIndex = getIndex(specXjIndex, specId_data_4, picImageMap, CLASS_IDS.get(3), true);

                } else {
                    xxlLog.accept("spec no 25 photo: " + specId);
                }
            }
        }

        specPhotosMap.forEach((key, value) -> {
            if (!value.isEmpty()) {
                List<Spec25PhotoDto> spec25PhotoDtos = value.stream().map(this::toSpec25PhotoDto).toList();
                update(makeParam(key), spec25PhotoDtos);
                ThreadUtil.sleep(5);
                xxlLog.accept("refresh spec 25 photos: " + key);
            }
        });
    }

    private int getIndex(int index, List<CarPhotoDto> data, Map<Integer, Car25PhotoEntity> picImageMap, int claType, boolean isSpec) {

        if (CollectionUtils.isEmpty(data)) {
            return index;
        }
        for (CarPhotoDto photoDto : data) {
            Car25PhotoEntity car25PhotoEntity = picImageMap.get(photoDto.getPicId());
            if (car25PhotoEntity != null && car25PhotoEntity.getType() == claType) {
                if (isSpec) {
                    car25PhotoEntity.setSpecInd(index);
                } else {
                    car25PhotoEntity.setSeriesInd(index);
                }
            }
            index++;
        }
        return index;
    }


    private Spec25PhotoDto toSpec25PhotoDto(Car25PhotoEntity entity) {
        Spec25PhotoDto dto = new Spec25PhotoDto();
        dto.setPicId(entity.getPicId());
        dto.setOrder(entity.getOrderCls());
        dto.setPic(ImageUtils.getFullImagePathWithoutReplace(entity.getPicPath()));
        dto.setType(entity.getType());
        dto.setSeriesInd(entity.getSeriesInd());
        dto.setSpecInd(entity.getSpecInd());
        return dto;
    }


}
