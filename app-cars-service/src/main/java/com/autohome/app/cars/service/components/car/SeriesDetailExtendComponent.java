package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsNewCarResult;
import com.autohome.app.cars.common.carconfig.Level;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailExtendDto;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TODO check ??? 如果redis不存在，应该怎么处理？
 */
@Component
@DBConfig(tableName = "series_detail_extend")
@RedisConfig
public class SeriesDetailExtendComponent extends BaseComponent<SeriesDetailExtendDto> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecMapper specMapper;
    @Autowired
    CmsApiClient cmsApiClient;
    @Autowired
    SeriesAttentionComponent seriesAttentionComponent;

    static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesDetailExtendDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesDetailExtendDto>> getList(List<Integer> seriesId) {
        return baseGetListAsync(seriesId.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    /**
     * 从库里拉取所有数据到redis&db
     */
    public void refreshAll(Consumer<String> log) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();

        allSeries.forEach(series -> {
            update(makeParam(series.getId()), builder(series));
            log.accept(series.getId() + " success");
            ThreadUtil.sleep(20);
        });
    }

    public void refresh(int seriesId) {
        try {
            SeriesEntity entity = seriesMapper.getSeries(seriesId);
            SeriesDetailExtendDto dto = builder(entity);
            update(makeParam(seriesId), dto);
        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
        }
    }

    private SeriesDetailExtendDto builder(SeriesEntity entity) {
        SeriesDetailExtendDto dto = new SeriesDetailExtendDto();
        dto.setId(entity.getId());
        dto.setNewenergySeriesId(entity.getNewenergySeriesId());
        dto.setDelegate25SpecId(entity.getDelegate25SpecId());

        return dto;
    }
}
