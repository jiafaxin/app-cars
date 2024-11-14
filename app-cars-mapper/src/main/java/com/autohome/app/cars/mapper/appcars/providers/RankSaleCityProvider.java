package com.autohome.app.cars.mapper.appcars.providers;

import com.autohome.app.cars.common.utils.StrPool;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author zhangchengtao
 * @date 2024/5/6 11:22
 */
public class RankSaleCityProvider {
    public static final String BASE_SQL = "SELECT id, brandid, seriesid, GROUP_CONCAT(CONCAT(energy_type, '_', salecnt) SEPARATOR '|') as energy_sale_count, min_guidance_price, max_guidance_price, `month`, cityid, SUM(salecnt) AS salecnt, energy_type, manu_type, `level`, is_newenergy, created_stime, modified_stime FROM (SELECT id, brandid, seriesid, SUM(salecnt) AS salecnt, min_guidance_price, max_guidance_price, `month`, cityid, energy_type, manu_type, `level`, is_newenergy, created_stime, modified_stime FROM rank_sale_city";

    public static final String LAST_MONTH_CITY_ITEM_SQL = "SELECT id, brandid, seriesid, `month`, cityid, created_stime, modified_stime FROM rank_sale_city ORDER BY id DESC LIMIT 1";

    /**
     * 获取最近一个月的日期SQL
     *
     * @return 获取最近一个月的日期SQL
     */
    public String getLastMonth() {
        return LAST_MONTH_CITY_ITEM_SQL;
    }

    /**
     * 通过条件查询月份数据
     *
     * @param beginMonth 开始月份
     * @param endMonth   结束月份
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
        sqlBuilder.append(" AND cityid = #{cityId}")
                .append(" group by seriesid, energy_type) as subquery GROUP BY seriesid ORDER BY salecnt DESC, seriesid LIMIT #{size}");
        return sqlBuilder.toString();
    }




}