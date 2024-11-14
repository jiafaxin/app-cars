package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.ShowEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface ShowMapper {
    @Select("SELECT Id ,Name  FROM Exposition.dbo.Shows WITH(NOLOCK)")
    List<ShowEntity> getShowNames();
}
