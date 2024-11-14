package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.SaleWeekEnergyTypeRankSourceEntity;
import com.autohome.app.cars.mapper.appcars.providers.RankSaleWeekProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/5/6 18:57
 */
@Mapper
@DS("appcars")
public interface RankSaleWeekMapper {

    @SelectProvider(type = RankSaleWeekProvider.class, method = "getLastWeek")
    RankSaleWeekSourceEntity getLastWeek();

    @SelectProvider(type = RankSaleWeekProvider.class, method = "getListByWeek")
    List<RankSaleWeekSourceEntity> getListByWeek(@Param("beginWeek") String beginWeek,
                                                 @Param("endWeek") String endWeek,
                                                 @Param("size") Integer size);

    /**
     * 查询车系销量趋势
     *
     * @param seriesId   车系ID
     * @param energyType 能源类型
     * @return 车系销量历史
     */
    @SelectProvider(type = RankSaleWeekProvider.class, method = "getWeekSeriesSaleHistory")
    List<RankSaleWeekSourceEntity> getSeriesSaleHistory(@Param("seriesId") Integer seriesId, @Param("energyType") String energyType);

    /**
     * 根据日期获取本周数据下车系id列表
     *
     * @param week_day 日期(每周的周二)
     * @return
     */
    @Select("select id,brandid,seriesid,week_day,salecnt,energy_type,is_newenergy,created_stime,modified_stime seriesid from rank_sale_week where week_day=#{week_day}")
    List<SaleWeekEnergyTypeRankSourceEntity> getSeriesIdListByWeekDay(@Param("week_day") String week_day);

    /**
     * 获取最近8周时间列表
     *
     * @param seriesId 车系id
     * @return
     */
    @Select("SELECT week_day FROM rank_sale_week WHERE seriesid=#{seriesId} GROUP BY week_day ORDER BY week_day DESC LIMIT 8")
    List<String> getLastWeekDayList(@Param("seriesId") Integer seriesId);

    /**
     * 获取所有的车系id
     *
     * @return
     */
    @Select("SELECT seriesid FROM rank_sale_week GROUP BY seriesid")
    List<Integer> getAllSeriesId();

    @Select("SELECT week_day FROM rank_sale_week where week_day>#{day} GROUP BY week_day ORDER BY week_day DESC")
    List<String> getWeekList(String day);

    @Select("SELECT week_day FROM rank_sale_week  GROUP BY week_day ORDER BY week_day DESC LIMIT #{limit}")
    List<String> getWeekListLimit(int limit);

    @Select("select week_day,SUM(salecnt) AS salecnt from rank_sale_week where brandid=#{brandid} GROUP BY week_day ORDER BY week_day DESC LIMIT #{limit}")
    List<SaleWeekEnergyTypeRankSourceEntity>getBrandHistory(@Param("brandid") Integer brandid,@Param("limit") Integer limit);
}
