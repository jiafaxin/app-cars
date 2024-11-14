package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.ColorInfoEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface InnerFctColorMapper {

    @Select("select id,ColorName as [name],ColorValue as [value] from InnerFctColor with(nolock)")
    List<ColorInfoEntity> getAllColorInfo();

}
