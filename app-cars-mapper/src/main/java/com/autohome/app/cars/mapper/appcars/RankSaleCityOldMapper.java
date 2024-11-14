package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.mapper.appcars.providers.RankSaleCityOldProvider;
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
public interface RankSaleCityOldMapper {
    @SelectProvider(type = RankSaleCityOldProvider.class, method = "getLastMonth")
    RankSaleCitySourceEntity getLastMonth();

    @SelectProvider(type = RankSaleCityOldProvider.class, method = "getSaleCountByCondition")
    List<RankSaleCitySourceEntity> getSaleCountByCondition(@Param("beginMonth") String beginMonth,
                                                            @Param("endMonth") String endMonth,
                                                            @Param("cityId") Integer cityId,
                                                            @Param("size") Integer size);

    @SelectProvider(type = RankSaleCityOldProvider.class, method = "getLastOneByMonth")
    RankSaleCitySourceEntity getLastOneByMonth(@Param("month") String month);


    @SelectProvider(type = RankSaleCityOldProvider.class, method = "getAllCityByMonth")
    List<RankSaleCitySourceEntity> getAllCityByMonth(String month, Integer isNewEnergy);
}
