package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliEntity;
import com.autohome.app.cars.mapper.appcars.entities.EnergyKoubeiEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("appcars")
public interface EnergyKoubeiMapper {

    @Select("select * from new_energy_koubei where 1=1")
    List<EnergyKoubeiEntity> getAll();

}
