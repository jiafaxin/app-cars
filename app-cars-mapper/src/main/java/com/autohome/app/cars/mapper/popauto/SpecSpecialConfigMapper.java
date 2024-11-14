package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.ConfigSpecificEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecColorListEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecSpecialConfigMapper {

    @Select("<script>\n" +
            "select b.ItemId as itemId,a.Name as itemName,BaikeUrl as baiKeUrl,BaikeId as baiKeId,b.SpecId as specId,ItemValue as itemValue,Price as price,sort\n" +
            " from Config_Specific_ConfigItem a with(nolock) inner join Config_Specific_SpecItemRelation b with(nolock)                           \n" +
            "on a.id=b.ItemId  \n" +
            "where b.SpecId in\n" +
            "<foreach collection='specIds' item='specId' open='(' separator=',' close=')'>\n" +
            "#{specId}\n" +
            "</foreach>\n" +
            "order by sort \n" +
            "\n" +
            "</script>")
    List<ConfigSpecificEntity> getConfigSpecificBySpecIds(List<Integer> specIds);

    @Select("select b.ItemId as itemId,a.Name as itemName,BaikeUrl as baiKeUrl,BaikeId as baiKeId,b.SpecId as specId,ItemValue as itemValue,Price as price,sort \n" +
            "from Config_Specific_ConfigItem a with(nolock) inner join Config_Specific_SpecItemRelation b with(nolock)                           \n" +
            "on a.id=b.ItemId where b.SpecId = #{specId} order by sort ")
    List<ConfigSpecificEntity> getConfigSpecificBySpecId(int specId);


    @Select("select DISTINCT specid from Config_Specific_SpecItemRelation")
    List<Integer> getSpecItemRelationAll();

}
