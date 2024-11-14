package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CarTimeAboutPicEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.providers.SeriesTimeAxisProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Date;
import java.util.List;

/**
 * @author : zzli
 * @description : 车系日历
 * @date : 2024/4/26 10:40
 */
@Mapper
@DS("popauto")
public interface SeriesTimeAxisMapper {
    /**
     * 图片首发
     */
    @SelectProvider(value = SeriesTimeAxisProvider.class, method = "getPicFirstAddTime")
    CarTimeAboutPicEntity getPicFirstAddTime(int seriesId);

    /**
     * 图片更新
     */
    @SelectProvider(value = SeriesTimeAxisProvider.class, method = "getPicUpdateTime")
    CarTimeAboutPicEntity getPicUpdateTime(@Param("seriesId") Integer seriesId, @Param("outDate") String outDate);

    /**
     * 配置首发
     */
    @SelectProvider(value = SeriesTimeAxisProvider.class, method = "getParamFirstTime")
    Date getParamFirstTime(int seriesId);

    /**
     * 配置更新
     */
    @SelectProvider(value = SeriesTimeAxisProvider.class, method = "getParamUpdateTime")
    Date getParamUpdateTime(@Param("seriesId") Integer seriesId, @Param("outDate") String outDate);

    @SelectProvider(value = SeriesTimeAxisProvider.class, method = "getSpecList")
    List<SpecEntity> getSpecAll();
}
