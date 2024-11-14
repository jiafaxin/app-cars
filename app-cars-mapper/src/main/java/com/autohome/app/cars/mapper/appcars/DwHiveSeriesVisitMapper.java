package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.SeriesSpecVisitEntity;
import com.autohome.app.cars.mapper.appcars.entities.SeriesBenchmarkingVisitEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/8
 */
@Mapper
@DS("appcars")
public interface DwHiveSeriesVisitMapper {

    @Select("SELECT dt, series_id, series_name, compare_data FROM dw_hive_series_benchmarking_visit WHERE dt = #{dt}")
    List<SeriesBenchmarkingVisitEntity> getAllBenchmarkingVisitByDt(@Param("dt") String dt);

    @Select("SELECT dt, series_id, series_name, all_uv, spec_uv FROM dw_hive_series_spec_visit WHERE dt = #{dt}")
    List<SeriesSpecVisitEntity> getAllSpecVisitByDt(@Param("dt") String dt);
}
