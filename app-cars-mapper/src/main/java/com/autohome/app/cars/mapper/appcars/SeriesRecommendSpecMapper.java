package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.SeriesRecommendSpecEntity;
import com.autohome.app.cars.mapper.appcars.providers.SeriesRecommendSpecProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("appcars")
public interface SeriesRecommendSpecMapper {

    @SelectProvider(type = SeriesRecommendSpecProvider.class, method = "getListBySeriesIdList")
    List<SeriesRecommendSpecEntity> getListBySeriesIdList(int cityId, List<Integer> seriesIdList);

    @SelectProvider(type = SeriesRecommendSpecProvider.class, method = "getListBySeriesId")
    List<SeriesRecommendSpecEntity> getListBySeriesId(Integer seriesId);


}
