package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecConfigItemRelaPic;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigPriceEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigRelationEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigSubItemEntity;
import com.autohome.app.cars.mapper.popauto.providers.SpecConfigProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author : zzli
 * @description : 配置
 * @date : 2024/5/14 20:25
 */
@Mapper
@DS("popauto")
public interface SpecConfigMapper {
    @SelectProvider(value = SpecConfigProvider.class, method = "getSpecStardConfigSql")
    List<SpecConfigItemRelaPic> getSpecStardConfig(String specIds);

    @SelectProvider(value = SpecConfigProvider.class,method = "getSpecConfigRelations")
    List<SpecConfigRelationEntity> getSpecConfigRelations(int specId);

    @SelectProvider(value = SpecConfigProvider.class,method = "getSpecConfigSubItems")
    List<SpecConfigSubItemEntity> getSpecConfigSubItems(int specId);



    @Select("SELECT DISTINCT SpecId, ItemId ,SubItemId,Price FROM ConfigSpecPrice WITH(NOLOCK) WHERE SpecId = #{specId} ORDER BY SpecId,ItemId,SubItemId")
    List<SpecConfigPriceEntity> getSpecConfigPrice(int specId);
}
