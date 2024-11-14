package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.ParamItemPo;
import com.autohome.app.cars.mapper.popauto.entities.ParamTypePo;
import com.autohome.app.cars.mapper.popauto.entities.ParamUnionPo;
import com.autohome.app.cars.mapper.popauto.providers.SpecParamInfoProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : lihongchen
 * @description : 车型参数信息
 * @date : 2024/4/19 10:45
 */
@Mapper
@DS("popauto")
public interface SpecParamInfoMapper {

    @SelectProvider(value = SpecParamInfoProvider.class, method = "getParamsBySpecIds")
    List<ParamUnionPo> getParamsBySpecIds(@Param("specIdsStr")String specIdsStr, @Param("isCV") boolean isCV);

    @SelectProvider(value = SpecParamInfoProvider.class, method = "getAllParamType")
    LinkedList<ParamTypePo> getAllParamType();

    @SelectProvider(value = SpecParamInfoProvider.class, method = "getAllParamItems")
    LinkedList<ParamItemPo> getAllParamItems();

}
