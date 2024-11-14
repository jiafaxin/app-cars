package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.AttRankNewCarEntity;
import com.autohome.app.cars.mapper.appcars.providers.AttRankNewCarProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Mapper
@DS("appcars")
public interface AttRankNewCarMapper {

    @Select(AttRankNewCarProvider.baseSql + "WHERE series_id = #{seriesId} ORDER BY dt DESC LIMIT 30; ")
    List<AttRankNewCarEntity> get30DaysBySeriesId(@Param("seriesId") int seriesId);

    @Select("SELECT series_id from att_rank_new_car where dt = #{dt}")
    List<Integer> getSeriesIdsByDt(@Param("dt") String dt);

    @Select("SELECT distinct(series_id) from att_rank_new_car;")
    List<Integer> getAllSeriesIds();

    @SelectProvider(type = AttRankNewCarProvider.class, method = "pageGetByLevelAndDt")
    List<AttRankNewCarEntity> pageGetByLevelAndDt(@Param("levelIds") String levelIds,
                                                  @Param("dt") String dt,
                                                  @Param("start") int start,
                                                  @Param("count") int count);

    @SelectProvider(type = AttRankNewCarProvider.class, method = "countByLevelAndDt")
    int countByLevelAndDt(@Param("levelIds") String levelIds,
                          @Param("dt") String dt);

    @SelectProvider(type = AttRankNewCarProvider.class, method = "getBySeriesIdsAndDt")
    List<AttRankNewCarEntity> getBySeriesIdsAndDt(@Param("seriesIds") String seriesIds,
                                                  @Param("dt") String dt);

    @SelectProvider(type = AttRankNewCarProvider.class, method = "getByDt")
    List<AttRankNewCarEntity> getByDt(@Param("dt") String dt);
}
