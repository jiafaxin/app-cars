package com.autohome.app.cars.mapper.appcars.providers;

import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author zhangchengtao
 * @date 2024/4/22 13:50
 */

public class RankSaleMonthProvider {


    public static final String BASE_SQL = "SELECT seriesid, GROUP_CONCAT( CONCAT( energy_type, '_', salecnt ) SEPARATOR '|' ) AS energy_sale_count, min_guidance_price, max_guidance_price, brandid,`month`,SUM(salecnt) AS salecnt, manu_type, `level`, is_newenergy, created_stime, modified_stime, is_del FROM" +
            "(SELECT seriesid, energy_type, SUM( salecnt ) AS salecnt,min_guidance_price,max_guidance_price,brandid,`month`,manu_type, `level`, is_newenergy, created_stime, modified_stime, is_del FROM rank_sale_month";
    public static final String HISTORY_SQL_SCHEME = "SELECT id, seriesid, energy_type, min_guidance_price, max_guidance_price, `month`, " +
            "SUM(salecnt) AS salecnt, manu_type,`level`, is_newenergy, is_del, created_stime, modified_stime, procnt FROM " +
            "rank_sale_month WHERE seriesid=#{seriesId}";
    public static final String LAST_MONTH_SQL = "SELECT `month` FROM rank_sale_month ORDER BY month DESC LIMIT 1";

    /**
     * 获取最近的一个月
     * @return SQL str
     */
    public String getLastMonth() {
        return LAST_MONTH_SQL;
    }

    /**
     * 通过条件查询月份数据
     * @param beginMonth 开始月份
     * @param endMonth 结束月份
     * @return String
     */
    public String getSaleCountByCondition(@Param("beginMonth") String beginMonth,
                                          @Param("endMonth") String endMonth) {
        StringBuilder sqlBuilder = new StringBuilder(BASE_SQL);
        // 如果起始月份和结束月份相同, 则使用 = 不同时使用BETWEEN
        if (StringUtils.hasLength(beginMonth) && beginMonth.equals(endMonth)) {
            sqlBuilder.append(" WHERE `month` = #{beginMonth}");
        } else {
            sqlBuilder.append(" WHERE `month` BETWEEN #{beginMonth} AND #{endMonth}");
        }
        sqlBuilder.append(" GROUP BY seriesid, energy_type) AS subquery GROUP BY seriesid ORDER BY salecnt DESC, seriesid LIMIT #{size}");
        return sqlBuilder.toString();
    }

    public String getMonthSeriesSaleHistory(@Param("energyType") String energyType) {
        return HISTORY_SQL_SCHEME + (!"0".equals(energyType) ? " AND energy_type IN(" + energyType + ")" : "") + " GROUP BY `month` ORDER BY month LIMIT 1000";
    }


}
