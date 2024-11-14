package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecParamConfigPicInfoEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecParamConfigPicInfoMapper {

    /**
     * 多车型参数配置可视化提示: 只返回在售和停产在售车型
     *
     * @param specids
     * @return
     */
    @Select("select SeriesId,specid,DataType,itemid,ItemName,valu,SubItemId,SubItemName,SubItemOrderCls,picid,picurl,SpecOrdercls from " +
            "Visual_ParamconfigView with(nolock) where specid in (${specids}) and  SpecState>=10 and SpecState<=30 ")
    List<SpecParamConfigPicInfoEntity> getParamConfigTipsBySpecIds(@Param("specids") String specids);

    /**
     * 所有车型参数配置可视化提示: 只返回在售和停产在售车型
     *
     * @return
     */
    @Select("select SeriesId,specid,DataType,itemid,ItemName,valu,SubItemId,SubItemName,SubItemOrderCls,picid,picurl,SpecOrdercls from " +
            "Visual_ParamconfigView with(nolock) where SpecState>=10 and SpecState<=30 ")
    List<SpecParamConfigPicInfoEntity> getAllParamConfigTips();

}
