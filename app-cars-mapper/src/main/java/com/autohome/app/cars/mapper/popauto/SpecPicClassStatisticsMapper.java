package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.app.cars.mapper.popauto.providers.SpecPicClassStatisticsProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecPicClassStatisticsMapper {

    @SelectProvider(value = SpecPicClassStatisticsProvider.class, method = "getSpecPicClassStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecPicClassStatisticsBySeriesId(int seriesId);

    @SelectProvider(value = SpecPicClassStatisticsProvider.class, method = "getCVSpecPicClassStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getCVSpecPicClassStatisticsBySeriesId(int seriesId);

}
