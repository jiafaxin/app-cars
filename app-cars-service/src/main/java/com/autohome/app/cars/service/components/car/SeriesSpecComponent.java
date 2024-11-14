package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "series_spec")
public class SeriesSpecComponent extends BaseComponent<SeriesSpecDto> {


    @Autowired
    SpecMapper specMapper;

    final String paramName = "seriesId";

    public CompletableFuture<SeriesSpecDto> getAsync(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public SeriesSpecDto get(int seriesId) {
        return baseGet(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesSpecDto>> get(List<Integer> seriesId) {
        return baseGetListAsync(seriesId.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    public void refreshAll(Consumer<String> log) {
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());

        loopSeries(10, seriesId -> {
            SeriesSpecDto dto = buildDto(seriesId, specAll.stream().filter(x -> x.getSeriesId() == seriesId).toList());
            update(makeParam(seriesId), dto);
        },log);
    }

    SeriesSpecDto buildDto(int seriesId, List<SpecEntity> entities) {
        SeriesSpecDto result = new SeriesSpecDto();
        result.setSeriesId(seriesId);

        for (var entity : entities) {
            SeriesSpecDto.Item item = new SeriesSpecDto.Item();
            item.setId(entity.getId());
            item.setState(entity.getState());
            item.setFuelType(entity.getFuelType());
            item.setMinPrice(entity.getMinPrice());
            result.getItems().add(item);
        }
        return result;
    }

    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(paramName, brandId).build();
    }
}
