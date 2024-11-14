package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.mapper.appcars.providers.RankSaleCityProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 城市榜Mapper
 */
@Mapper
@DS("appcars")
public interface RankSaleCityMapper {
    @SelectProvider(type = RankSaleCityProvider.class, method = "getLastMonth")
    RankSaleCitySourceEntity getLastMonth();

    @SelectProvider(type = RankSaleCityProvider.class, method = "getSaleCountByCondition")
    List<RankSaleCitySourceEntity> getSaleCountByCondition(@Param("beginMonth") String beginMonth,
                                                            @Param("endMonth") String endMonth,
                                                            @Param("cityId") String cityId,
                                                            @Param("size") Integer size);

}
