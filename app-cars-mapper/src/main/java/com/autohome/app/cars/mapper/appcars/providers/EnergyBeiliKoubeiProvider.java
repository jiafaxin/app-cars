package com.autohome.app.cars.mapper.appcars.providers;

import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author chengjincheng
 * @date 2024/3/28
 */
public class EnergyBeiliKoubeiProvider {

    public final static String baseSql = "SELECT series_id, brand_id, spec_id, level_id, state, season, " +
            "drive_range, energy_cost, bl_zone_id, kb_zone_id, energyType, dataVersion, official_range, spec_name, min_price, max_price" +
            " FROM  ";

    public String pageGetByCondition(@Param("typeid") int typeid,
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
                                     @Param("count") int count) {
        StringBuilder sql = new StringBuilder();
        sql.append(baseSql);
        sql.append(typeid == 1 ? "rank_drive_range " : "rank_energy_cost ");
        sql.append("WHERE 1=1 ");
        if (!StringUtils.isEmpty(energytype) && !"0".equals(energytype)) {
            sql.append("AND energyType  in (${energytype})  ");
        }
        if (min_price > 0) {
            sql.append("AND max_price >= #{min_price} ");
        }
        if (max_price > 0) {
            sql.append("AND min_price <= #{max_price} ");
        }
        if (brandid > 0) {
            sql.append("AND brand_id =#{brandid} ");
        }
        if (season > 0) {
            sql.append("AND season =#{season} ");
        }
        if (issale == 1) {
            sql.append("AND state in (20,30) ");
        }

        if (!StringUtils.isEmpty(bl_zone_id) && !StringUtils.isEmpty(kb_zone_id)) {
            sql.append("AND bl_zone_id =#{bl_zone_id} AND kb_zone_id = #{kb_zone_id}");
        }
        if (!StringUtils.isEmpty(levelids) && !"0".equals(levelids)) {
            sql.append("AND level_id IN (${levelids}) ");
        }

        sql.append(typeid == 1 ? " ORDER BY drive_range desc " : " ORDER BY energy_cost asc ");
        sql.append("LIMIT #{start}, #{count} ");
        return sql.toString();
    }

    public String countByByCondition(@Param("typeid") int typeid,
                                     @Param("min_price") int min_price,
                                     @Param("max_price") int max_price,
                                     @Param("energytype") String energytype,
                                     @Param("brandid") int brandid,
                                     @Param("bl_zone_id") String bl_zone_id,
                                     @Param("kb_zone_id") String kb_zone_id,
                                     @Param("season") int season,
                                     @Param("issale") int issale,
                                     @Param("levelids") String levelids) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select COUNT(1) from ");
        sql.append(typeid == 1 ? "rank_drive_range " : "rank_energy_cost ");
        sql.append("WHERE 1=1 ");
        if (!StringUtils.isEmpty(energytype) && !"0".equals(energytype)) {
            sql.append("AND energyType  in (${energytype})  ");
        }
        if (min_price > 0) {
            sql.append("AND max_price >= #{min_price} ");
        }
        if (max_price > 0) {
            sql.append("AND min_price <= #{max_price} ");
        }
        if (brandid > 0) {
            sql.append("AND brand_id =#{brandid} ");
        }
        if (season > 0) {
            sql.append("AND season =#{season} ");
        }
        if (issale == 1) {
            sql.append("AND state in (20,30) ");
        }
        if (!StringUtils.isEmpty(bl_zone_id) && !StringUtils.isEmpty(kb_zone_id)) {
            sql.append("AND bl_zone_id =#{bl_zone_id} AND kb_zone_id = #{kb_zone_id} ");
        }
        if (!StringUtils.isEmpty(levelids) && !"0".equals(levelids)) {
            sql.append("AND level_id IN (${levelids}) ");
        }

        sql.append(typeid == 1 ? " ORDER BY drive_range desc " : " ORDER BY energy_cost asc ");
        return sql.toString();
    }

}
