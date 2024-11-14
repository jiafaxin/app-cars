package com.autohome.app.cars.mapper.appcars;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/6/20
 */
@Mapper
@DS("appcars")
public interface SeriesCityAskPriceNewMapper {

    @Select("SELECT seriesId FROM series_city_askprice_new WHERE cityId = #{cityId} AND is_del = 0")
    List<Integer> getSeriesIdsByCity(@Param("cityId") int cityId);
}
