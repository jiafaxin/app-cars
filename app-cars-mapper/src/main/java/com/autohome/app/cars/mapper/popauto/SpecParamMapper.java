package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.mapper.popauto.providers.SpecParamProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author : zzli
 * @description : 车型参配
 * @date : 2024/2/26 14:35
 */
@Mapper
@DS("popauto")
public interface SpecParamMapper {
    @SelectProvider(value = SpecParamProvider.class, method = "getSpecPartParamBySeriesId")
    List<SpecParamEntity> getSpecPartParamBySeriesId(@Param("seriesid") Integer seriesid, @Param("level") Integer level);

    @SelectProvider(value = SpecParamProvider.class, method = "getSpecPartParam1BySeriesId")
    List<SpecParamEntity> getSpecPartParam1BySeriesId(@Param("seriesid") Integer seriesid, @Param("level") Integer level);

    @SelectProvider(value = SpecParamProvider.class, method = "getSpecParamAll")
    List<SpecParamEntity> getSpecParamAll(@Param("paramId") Integer paramId, @Param("specId") Integer specId);

    @SelectProvider(value = SpecParamProvider.class, method = "getSpecSubParamAll")
    List<SpecParamEntity> getSpecSubParamAll(@Param("paramId") Integer paramId, @Param("specId") Integer specId);

    @SelectProvider(value = SpecParamProvider.class, method = "getSpecParamBySeriesId")
    List<SpecParamEntity> getSpecParamBySeriesId(@Param("seriesId") Integer seriesId, @Param("level") Integer level, @Param("paramId") Integer paramId);

    @SelectProvider(value = SpecParamProvider.class, method = "getSpecSubParamBySeriesId")
    List<SpecParamEntity> getSpecSubParamBySeriesId(@Param("seriesId") Integer seriesId, @Param("level") Integer level, @Param("paramId") Integer paramId);
    @SelectProvider(value = SpecParamProvider.class, method = "getSpecHighLightParamSql")
    List<SpecParamEntity> getSpecHighLightParam(String specIds);
}
