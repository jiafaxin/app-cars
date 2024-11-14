package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.BeiliKoubeiEntity;
import com.autohome.app.cars.mapper.appcars.entities.ZoneMappingEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("appcars")
public interface
EnergyZoneMappingMapper {

    @Select("select bl_zone_id,kb_zone_id from new_energy_zone_mapping group by bl_zone_id,kb_zone_id  order by kb_zone_id asc,bl_zone_id desc;")
    List<ZoneMappingEntity> getMapping();

}
