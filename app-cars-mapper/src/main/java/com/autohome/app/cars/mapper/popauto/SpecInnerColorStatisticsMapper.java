package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.app.cars.mapper.popauto.providers.SpecInnerColorStatisticsProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecInnerColorStatisticsMapper {

    @SelectProvider(value = SpecInnerColorStatisticsProvider.class,method = "getSpecInnerColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecInnerColorStatisticsBySeriesId(int seriesId);

    @SelectProvider(value = SpecInnerColorStatisticsProvider.class,method = "getAllSpecInnerColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getAllSpecInnerColorStatisticsBySeriesId();
}
