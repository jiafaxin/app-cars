package com.autohome.app.cars.mapper;

import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("appcars")
public interface SeriesHotEventMapper {

    @Select("select * from new_energy_beili where 1=1")
    List<EnergyBeiliEntity> getAll();

}
