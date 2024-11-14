package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.BrightPointConfigEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface BrightpointsconfigMapper {

    @Select("SELECT *\n" +
            "FROM(\n" +
            "\tSELECT A.seriesid, A.title,A.url,ROW_NUMBER() OVER( PARTITION BY A.seriesid ORDER BY [Modified_STime] DESC) AS ROWNO\n" +
            "\tFROM brightpointsconfig AS A WITH(NOLOCK)\n" +
            ") AS T WHERE ROWNO = 1")
    List<BrightPointConfigEntity> getAllSeriesBrightPointConfigs();

}
