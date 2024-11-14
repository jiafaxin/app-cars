package com.autohome.app.cars.mapper.appcars.providers;

import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author zhangchengtao
 * @date 2024/5/6 11:22
 */
public class RankSaleWeekProvider {
    public static final String BASE_SQL = "SELECT  id, seriesid, GROUP_CONCAT(CONCAT(energy_type, '_', salecnt) SEPARATOR '|') as energy_sale_count, brandid, min_guidance_price, max_guidance_price, week_day,"
            + "SUM(salecnt) AS salecnt, manu_type, `level`, is_newenergy, created_stime, modified_stime,procnt FROM rank_sale_week ";
    public static final String HISTORY_SQL_SCHEME = "SELECT id, seriesid, brandid, min_guidance_price, max_guidance_price, week_day," +
            " SUM(salecnt) AS salecnt, manu_type, `level`, is_newenergy, created_stime, modified_stime, procnt FROM rank_sale_week WHERE seriesid = #{seriesId}";
    public static final String LAST_WEEK_SQL = "SELECT week_day FROM rank_sale_week ORDER BY week_day DESC LIMIT 1";

    /**
     * 查询月榜销量趋势SQL
     * @param energyType 能源类型
     * @return 获取最近一个周的日期SQL
     */
    public String getWeekSeriesSaleHistory(@Param("energyType") String energyType) {
        return HISTORY_SQL_SCHEME+ (!"0".equals(energyType) ? " AND energy_type IN(" + energyType + ")" : "") + " GROUP BY week_day ORDER BY week_day DESC LIMIT 8";
    }

    /**
     * 获取最近一个周的日期SQL
     * @return 获取最近一个周的日期SQL
     */
    public String getLastWeek() {
//        System.out.println("====RankSaleWeekProvider====getLastWeek======" + LAST_WEEK_SQL);
        return LAST_WEEK_SQL;
    }

    /**
     * 通过周查询周销量
     * @param beginWeek 查询起始周日期
     * @param endWeek 查询结束周日期
     * @param size 结果数量
     * @return 查询周销量
     */
    public String getListByWeek(@Param("beginWeek") String beginWeek,
                                @Param("endWeek") String endWeek,
                                @Param("size") Integer size) {
        StringBuilder sqlBuilder = new StringBuilder(BASE_SQL);
        // 如果起始月份和结束月份相同, 则使用 = 不同时使用BETWEEN
        if (StringUtils.hasLength(beginWeek) && beginWeek.equals(endWeek)) {
            sqlBuilder.append(" WHERE week_day = #{beginWeek}");
        } else {
            sqlBuilder.append(" WHERE week_day BETWEEN #{beginWeek} AND #{endWeek}");
        }
        sqlBuilder.append(" GROUP BY seriesid")
                .append(" ORDER BY salecnt DESC,seriesid")
                .append(" LIMIT #{size}");
//        System.out.println("====RankSaleWeekProvider====getListByWeek======" + sqlBuilder.toString());
        return sqlBuilder.toString();



    }

}