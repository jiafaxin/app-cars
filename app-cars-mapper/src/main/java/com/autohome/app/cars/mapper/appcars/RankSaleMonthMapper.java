package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.SaleMonthEnergyTypeRankSourceEntity;
import com.autohome.app.cars.mapper.appcars.providers.RankSaleMonthProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/4/22 13:50
 */
@Mapper
@DS("appcars")
public interface RankSaleMonthMapper {

    @SelectProvider(type = RankSaleMonthProvider.class, method = "getLastMonth")
    RankSaleMonthSourceEntity getLastMonth();

    @SelectProvider(type = RankSaleMonthProvider.class, method = "getSaleCountByCondition")
    List<RankSaleMonthSourceEntity> getSaleCountByCondition(@Param("beginMonth") String beginMonth,
                                                            @Param("endMonth") String endMonth,
                                                            @Param("size") Integer size);

    /**
     * 查询车系销量趋势
     *
     * @param seriesId   车系ID
     * @param energyType 能源类型
     * @return 车系销量历史
     */
    @SelectProvider(type = RankSaleMonthProvider.class, method = "getMonthSeriesSaleHistory")
    List<RankSaleMonthSourceEntity> getSeriesSaleHistory(@Param("seriesId") Integer seriesId, @Param("energyType") String energyType);


    /**
     * 根据月份获取当月数据
     *
     * @param month 月份
     * @return
     */
    @Select("select id,brandid,seriesid,`month`,salecnt,energy_type,is_newenergy,created_stime,modified_stime seriesid from rank_sale_month where `month`=#{month}")
    List<SaleMonthEnergyTypeRankSourceEntity> getSeriesIdListByMonth(@Param("month") String month);

    /**
     * 获取所有的月份
     *
     * @return
     */
    @Select("select `month` from rank_sale_month GROUP BY `month` order by `month` desc")
    List<String> getMonthList();

    @Select("select month,SUM(salecnt) AS salecnt from rank_sale_month where brandid=#{brandid} GROUP BY month ORDER BY month ")
    List<SaleMonthEnergyTypeRankSourceEntity>getBrandHistory(@Param("brandid") Integer brandid);

}
