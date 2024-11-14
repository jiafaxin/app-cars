package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.KeyValueDto;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SeriesPicMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesPicEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesPicDto;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系图片信息
 */
@Component
@DBConfig(tableName = "series_pic_count")
public class SeriesPicCountComponent extends BaseComponent<SeriesPicDto> {
    final String seriesPicIdParamName = "seriesId";

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesPicMapper seriesPicMapper;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesPicIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesPicDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }


    public CompletableFuture<List<SeriesPicDto>> getList(List<Integer> seriesId) {
        return baseGetListAsync(seriesId.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public void refreshAll(int totalMinutes, Consumer<String> log) {
        List<SeriesPicEntity> seriesPics = seriesPicMapper.getSeriesPicAll();
        List<KeyValueDto<Integer, String>> keyValueDtos = seriesMapper.getAllSeriesIdAndName();
        List<SeriesPicEntity> autoShowPicList;

        if (autoShowConfig.IsBetweenDate()) {
            autoShowPicList = seriesPicMapper.getSeriesAutoShowPicCountAll(autoShowConfig.getAutoshowid());
        } else {
            autoShowPicList = new ArrayList<>();
        }

        //filter 的性能没有那么好，转成map在处理更好
        loopSeries(totalMinutes, seriesId -> {
            List<CarPhotoViewEntity> seriesPicTop5 = seriesPicMapper.getSeriesPicTop5BySeriesId(seriesId);
            update(makeParam(seriesId),
                    builder(seriesId,
                            seriesPics.stream().filter(p -> p.getSeriesId() == seriesId).collect(Collectors.toList()),
                            autoShowPicList.stream().filter(p -> p.getSeriesId() == seriesId).findFirst().orElse(null),
                            keyValueDtos.stream().filter(p -> p.getKey().equals(seriesId)).findFirst().orElse(null),
                            seriesPicTop5));
            log.accept(seriesId + "");
        }, log);

    }

    SeriesPicDto builder(int seriesId, List<SeriesPicEntity> entitys, SeriesPicEntity autoShowPic, KeyValueDto<Integer, String> keyValueDto, List<CarPhotoViewEntity> seriesPicTop5Map) {
        SeriesPicDto dto = new SeriesPicDto();
        Map<Integer, List<CarPhotoViewEntity>> picClassMap = seriesPicTop5Map.stream().collect(Collectors.groupingBy(CarPhotoViewEntity::getPicClass));

        dto.setItems(entitys.stream().map(entity -> new SeriesPicDto.Item() {{
            setId(entity.getPicId());
            setName(entity.getPicName());
            setCount(entity.getPicCount());
            setPicItems(picClassMap.containsKey(entity.getPicId()) ? picClassMap.get(entity.getPicId()).stream().map(x ->
                            new SeriesPicDto.PicItem(x.getPicId(), ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), x.getSpecId()))
                    .collect(Collectors.toList()) : new ArrayList<>());
        }}).collect(Collectors.toList()));
        dto.setAutoShowPicCount(autoShowPic == null ? 0 : autoShowPic.getPicCount());
        dto.setSeriesId(seriesId);
        if(null != keyValueDto){
            dto.setSeriesName(StringEscapeUtils.unescapeHtml4(keyValueDto.getValue()));
        }
        return dto;
    }
}
