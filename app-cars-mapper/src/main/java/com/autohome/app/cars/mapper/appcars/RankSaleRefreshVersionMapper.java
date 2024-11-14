package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankSaleRefreshVersionEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

/**
 * Created by dx on 2024/9/5
 */
@Mapper
@DS("appcars")
public interface RankSaleRefreshVersionMapper {
    @Select("select * from rank_sale_refreshversion where is_del=0 limit 1")
    RankSaleRefreshVersionEntity getRankSaleRefreshVersion();

    @Update("update rank_sale_refreshversion set currentversion=#{currentversion},preversion=#{preversion} where is_del=0")
    int updateRankSaleRefreshVersion(@Param("currentversion") int currentversion, @Param("preversion") int preversion);

}
