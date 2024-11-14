package com.autohome.app.cars.mapper.car;

import com.autohome.app.cars.mapper.car.entities.HqOrderEntity;
import com.autohome.app.cars.mapper.car.entities.HqPointEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("car")
public interface HqOrderMapper {

    @Select("select id,series_id,spec_id,color_id,inner_color_id\n" +
            "from high_quality_order\n" +
            "where is_del = 0 and series_id = #{seriesId};")
    List<HqOrderEntity> getBySeriesId(int seriesId);

    @Select("select distinct series_id from high_quality_order where is_del = 0;")
    List<Integer> getAllSeriesIds();
}
