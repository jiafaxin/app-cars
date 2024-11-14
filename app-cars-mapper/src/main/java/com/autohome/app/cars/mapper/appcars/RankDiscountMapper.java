package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankDiscountEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by dx on 2024/6/12
 */
@Mapper
@DS("appcars")
public interface RankDiscountMapper {
    @Select("select * from rank_discount where cityId=#{cityId} and is_del=0")
    RankDiscountEntity getInfoByCityId(@Param("cityId") Integer cityId);
}
