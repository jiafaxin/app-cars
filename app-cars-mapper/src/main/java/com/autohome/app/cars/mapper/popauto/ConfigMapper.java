package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.common.utils.KeyValueDto;
import com.autohome.app.cars.mapper.popauto.entities.ConfigBaseEntity;
import com.autohome.app.cars.mapper.popauto.entities.ConfigItemEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface ConfigMapper {

    @Select("SELECT B.TypeId,B.id as ItemId,B.DynamicShow,C.Name as typeName,B.Name as itemName,B.DisplayType,B.logo,CVIsShow,IsShow\n" +
            "FROM ConfigItem AS B WITH(NOLOCK)\n" +
            "\t INNER JOIN ConfigType AS C WITH(NOLOCK) ON B.TypeId=C.Id\n" +
            "WHERE  B.CVIsShow =1 OR B.IsShow=1\n" +
            "ORDER BY C.Sort,B.Sort")
    List<ConfigItemEntity> getAllConfig();

    @Select("SELECT Id AS [key],Value FROM ConfigItemValue WITH(NOLOCK)")
    List<KeyValueDto<Integer,String>> getAllConfigItemValues();

    @Select("SELECT Id AS [key], Name AS [value]  FROM ConfigSubItem WITH(NOLOCK)")
    List<KeyValueDto<Integer,String>> getConfigSubItems();

    @Select("SELECT A.typeid, A.Id,A.Name,A.displaytype,A.isshow,A.cvisshow,A.Sort AS itemorder,B.Sort AS typeorder FROM ConfigItem AS A  WITH(NOLOCK)\n" +
            "                                                INNER JOIN ConfigType AS B WITH(NOLOCK) ON A.TypeId = B.Id WHERE A.IsShow =1  OR A.CVIsShow=1")
    List<ConfigBaseEntity> getConfigItemAll();

    @Select("SELECT Id as [key],  Name as [value] FROM ConfigItem WITH(NOLOCK)")
    List<KeyValueDto<Integer,String>> getAllConfigItem();


}
