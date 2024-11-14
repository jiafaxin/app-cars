package com.autohome.app.cars.mapper.car;

import com.autohome.app.cars.mapper.car.entities.HqPointEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("car")
public interface HqVideoPointMapper {

    @Select("SELECT T.id, F.id AS fId, S.id as sId, T.id as id, T.sort,T.point_name as name \n" +
            "FROM high_quality_video_point T\n" +
            "         inner join high_quality_photo_subtype S on s.id = T.photo_sub_type_id\n" +
            "         inner join high_quality_photo_type F on S.type_id = f.id\n" +
            "WHERE T.is_del = 0\n" +
            "  AND S.is_del = 0\n" +
            "  AND F.is_del = 0\n" +
            "  AND T.photo_sub_type_id > 0; ")
    List<HqPointEntity> getPoints();

}
