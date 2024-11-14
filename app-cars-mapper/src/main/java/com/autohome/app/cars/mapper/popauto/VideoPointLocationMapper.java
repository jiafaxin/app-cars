package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.PointParamConfigEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface VideoPointLocationMapper {

    @Select("select  A.pointlocationid,A.datatype,A.paramconfigid,A.paramconfigname,C.buId from Video_PointLocationRelationParamConfig  as A  with(nolock) \n" +
            "inner join Video_PointLocation as B  with(nolock) on A.pointlocationid = B.pointlocation_id\n" +
            "inner join Video_Type as C with(nolock) on C.typeid = B.pointlocation_typeid\n" +
            "where C.buid = #{buId} and B.is_del = 0 and C.is_del = 0")
    List<PointParamConfigEntity> getPointRelationParamConfigByBuId(@Param("buId") int buId);

    @Select("select distinct buId from Video_Type where is_del=0")
    List<Integer> getBuIdAll();
}
