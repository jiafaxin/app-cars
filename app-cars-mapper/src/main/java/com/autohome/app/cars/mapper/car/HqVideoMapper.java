package com.autohome.app.cars.mapper.car;

import com.autohome.app.cars.mapper.car.entities.HqOrderEntity;
import com.autohome.app.cars.mapper.car.entities.HqVideoBaseEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("car")
public interface HqVideoMapper {

    @Select("select id,mid,point_id,logo,modified_stime\n" +
            "from high_quality_video\n" +
            "where is_del = 0 and publish_state = 10 and check_state = 1 and order_id = #{orderId};")
    List<HqVideoBaseEntity> getBySeriesId(int seriesId);

}
