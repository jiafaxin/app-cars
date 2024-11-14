package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.SpecCityPriceHistoryEntity;
import com.autohome.app.cars.mapper.appcars.providers.SpecCityPriceHisProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/8/14 15:37
 */
@Mapper
@DS("appcars")
public interface SpecCityPriceHistoryMapper {
    @SelectProvider(type = SpecCityPriceHisProvider.class, method = "selectBySpecIdIn")
    List<SpecCityPriceHistoryEntity> selectBySpecIdIn(String specIds);

    @SelectProvider(type = SpecCityPriceHisProvider.class, method = "selectByCityIdAndSpecIdIn")
    List<SpecCityPriceHistoryEntity> selectByCityIdAndSpecIdIn(int cityId, String specIds);
}
