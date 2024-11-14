package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CarLevelEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/6
 */
@Mapper
@DS("popauto")
public interface CarLevelMapper {

    @Select("SELECT Id ,Name ,Dir,[Description] FROM car_spec_jb WITH(NOLOCK)\n" +
            "UNION ALL\n" +
            "SELECT 9 AS id,'SUV' AS name,null as Dir,null as [Description]")
    List<CarLevelEntity> getAllLevel();
}
