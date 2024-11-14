package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.BeiliKoubeiEntity;
import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliKoubeiEntity;
import com.autohome.app.cars.mapper.appcars.providers.EnergyBeiliKoubeiProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author : zzli
 * @description : 北理、口碑新能源数据
 * @date : 2024/2/27 12:05
 */
@Mapper
@DS("appcars")
public interface EnergyBeiliKoubeiMapper {

    /**
     * 获取城市对应的区域id
     */
    @Select("SELECT city_id,kb_zone_id from new_energy_zone_mapping")
    List<BeiliKoubeiEntity.CityZone> getZoneIdByCity();

    @Select("SELECT series_id,spec_id,season,drive_range,energy_cost,zone_id from new_energy_beili_koubei_union")
    List<BeiliKoubeiEntity> getEnergyBeiliKoubeiList();

    //dataVersion
    @Update("update new_energy_beili_koubei\n" +
            "set drive_range = #{drive_range},\n" +
            "    energy_cost = #{energy_cost},\n" +
            "    state = #{state},\n" +
            "    is_del = 0,\n" +
            "    dataVersion = #{dataVersion},\n" +
            "    dataFrom = #{dataFrom},\n" +
            "    official_range = #{official_range},\n" +
            "    is_max_range = #{is_max_range},\n" +
            "    spec_name = #{spec_name},\n" +
            "    min_price = #{min_price},\n" +
            "    max_price = #{max_price},\n" +
            "    modified_stime = NOW()\n" +
            "where spec_id = #{spec_id} and season = #{season} and bl_zone_id = #{bl_zone_id} and kb_zone_id = #{kb_zone_id};")
    int update(EnergyBeiliKoubeiEntity entity);

    @Insert("\n" +
            "insert into new_energy_beili_koubei (series_id, brand_id, spec_id, level_id, state, season, drive_range, energy_cost, is_del, created_stime, modified_stime, bl_zone_id, kb_zone_id, energyType,dataVersion,dataFrom,official_range,is_max_range,spec_name,min_price,max_price)\n" +
            "values (#{series_id}, #{brand_id}, #{spec_id}, #{level_id}, #{state}, #{season}, #{drive_range}, #{energy_cost}, 0, NOW(), NOW(), #{bl_zone_id}, #{kb_zone_id}, #{energyType},#{dataVersion},#{dataFrom},#{official_range},#{is_max_range},#{spec_name},#{min_price},#{max_price});")
    int insert(EnergyBeiliKoubeiEntity entity);

    @Update("update new_energy_beili_koubei set is_del = 1 where id = #{id}")
    int deleteOld(long id);

    @Select("SELECT * from new_energy_beili_koubei where dataVersion != #{version} and  is_del = 0")
    List<EnergyBeiliKoubeiEntity> getOldList(String version);

    @SelectProvider(type = EnergyBeiliKoubeiProvider.class, method = "pageGetByCondition")
    List<EnergyBeiliKoubeiEntity> pageGetByCondition(@Param("typeid") int typeid,
                                                     @Param("min_price") int min_price,
                                                     @Param("max_price") int max_price,
                                                     @Param("energytype") String energytype,
                                                     @Param("brandid") int brandid,
                                                     @Param("bl_zone_id") String bl_zone_id,
                                                     @Param("kb_zone_id") String kb_zone_id,
                                                     @Param("season") int season,
                                                     @Param("levelids") String levelids,
                                                     @Param("issale") int issale,
                                                     @Param("start") int start,
                                                     @Param("count") int count);

    @SelectProvider(type = EnergyBeiliKoubeiProvider.class, method = "countByByCondition")
    int countByByCondition(@Param("typeid") int typeid,
                           @Param("min_price") int min_price,
                           @Param("max_price") int max_price,
                           @Param("energytype") String energytype,
                           @Param("brandid") int brandid,
                           @Param("bl_zone_id") String bl_zone_id,
                           @Param("kb_zone_id") String kb_zone_id,
                           @Param("season") int season,
                           @Param("levelids") String levelids,
                           @Param("issale") int issale);

}
