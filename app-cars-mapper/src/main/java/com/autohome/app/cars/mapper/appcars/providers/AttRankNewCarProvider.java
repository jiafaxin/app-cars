package com.autohome.app.cars.mapper.appcars.providers;

import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author chengjincheng
 * @date 2024/3/28
 */
public class AttRankNewCarProvider {

    public final static String baseSql = "SELECT id, dt, series_id AS seriesId, series_name AS seriesName, " +
            "level_id AS levelId, att, on_time AS onTime, series_tag_id AS seriesTagId, article_id AS articleId, " +
            "rank_num AS rankNum, is_del AS isDel, created_stime AS createdStime, modified_stime AS modifiedStime " +
            "FROM att_rank_new_car ";

    public String pageGetByLevelAndDt(@Param("levelIds") String levelIds,
                                      @Param("dt") String dt,
                                      @Param("start") int start,
                                      @Param("count") int count) {
        StringBuilder sql = new StringBuilder();
        sql.append(baseSql);
        sql.append("WHERE dt =#{dt} ");
        if (!StringUtils.isEmpty(levelIds) && !"0".equals(levelIds)) {
            sql.append("AND level_id IN (" + levelIds + ") ");
        }
        sql.append("ORDER BY rank_num ASC LIMIT #{start}, #{count} ");
        return sql.toString();
    }

    public String countByLevelAndDt(@Param("levelIds") String levelIds,
                                    @Param("dt") String dt) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select COUNT(1) from att_rank_new_car ");
        sql.append("WHERE dt =#{dt} ");
        if (!StringUtils.isEmpty(levelIds) && !"0".equals(levelIds)) {
            sql.append("AND level_id IN (" + levelIds + ") ");
        }
        return sql.toString();
    }

    public String getBySeriesIdsAndDt(@Param("seriesIds") String seriesIds,
                                      @Param("dt") String dt) {
        StringBuilder sql = new StringBuilder();
        sql.append(baseSql);
        sql.append("WHERE dt =#{dt} ");
        if (!StringUtils.isEmpty(seriesIds)) {
            sql.append("AND series_id IN (" + seriesIds + ") ");
        }
        return sql.toString();
    }

    public String getByDt(@Param("dt") String dt) {
        StringBuilder sql = new StringBuilder();
        sql.append(baseSql);
        sql.append("WHERE dt =#{dt} ");
        sql.append("ORDER BY rank_num ASC ");
        return sql.toString();
    }
}
