package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.HighLightParamItem;
import com.autohome.app.cars.mapper.popauto.providers.SpecHighlightProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigItemRelaPic;
import java.util.List;

/**
 * @author : zzli
 * @description : 车型亮点
 * @date : 2024/3/5 18:01
 */
@Mapper
@DS("popauto")
public interface SpecHighlightMapper {
    /**
     * 获取车型亮点相关的配置
     */
    @SelectProvider(value = SpecHighlightProvider.class, method = "getSpecStandardConfigAndRelationPicListSql")
    Integer getSpecStandardConfigAndRelationPicList(@Param("specId") Integer specId);

    /**
     * 亮点配置,基本参数部分
     */

    @SelectProvider(value = SpecHighlightProvider.class, method = "getSpecParamListOfHighLightSql")
    Integer getSpecParamListOfHighLight(@Param("specId") Integer specId, @Param("fuelType") Integer fuelType);

    /**
     * 车系获取有点位信息的车型列表
     */

    @SelectProvider(value = SpecHighlightProvider.class, method = "get60picSpecIdListBySeriesSql")
    List<Integer> get60picSpecIdListBySeries(@Param("seriesId") Integer seriesId);

    /**
     * 车型标准配置与参配置可视化关联图片
     */
    @SelectProvider(value = SpecHighlightProvider.class, method = "getSpecStandardConfigAndRelationPicItemListSql")
    List<SpecConfigItemRelaPic> getSpecStandardConfigAndRelationPicItemList(int specId);

    /**
     * 取特定车型参数值-乘用车
     */
    @SelectProvider(value = SpecHighlightProvider.class, method = "getSpecParamItemListOfHighLightSql")
    List<HighLightParamItem> getSpecParamItemListOfHighLight(@Param("specId") Integer specId);
    /**
     * 取特定车型参数值-商用车
     */
    @SelectProvider(value = SpecHighlightProvider.class, method = "getSpecParamItemListOfHighLight_cvSql")
    List<HighLightParamItem> getSpecParamItemListOfHighLight_cv(@Param("specId") Integer specId);
}
