package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CrashCnCapSeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.CrashSeriesEntity;
import com.autohome.app.cars.mapper.popauto.providers.CrashTestSeriesProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/7/15
 */
@Mapper
@DS("popauto")
public interface CrashTestSeriesMapper {

    @SelectProvider(type = CrashTestSeriesProvider.class, method = "getDataSql")
    List<CrashSeriesEntity> getCrashTestData(int orderType, int standardId);


    @SelectProvider(type = CrashTestSeriesProvider.class, method = "getCrashCnCapTestData")
    List<CrashCnCapSeriesEntity> getCrashCnCapTestData();
}
