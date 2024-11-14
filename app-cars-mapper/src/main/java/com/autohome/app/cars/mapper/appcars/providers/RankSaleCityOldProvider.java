package com.autohome.app.cars.mapper.appcars.providers;

import com.autohome.app.cars.common.utils.StrPool;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

/**
 * @author zhangchengtao
 * @date 2024/5/6 11:22
 */
public class RankSaleCityOldProvider {
    public static final String BASE_SQL = "SELECT  id, brandid, seriesid, min_guidance_price, max_guidance_price, `month`, cityid, SUM(salecnt) AS salecnt, manu_type, `level`, is_newenergy, created_stime, modified_stime FROM rank_sale_city_old";

    public static final String LAST_MONTH_CITY_ITEM_SQL = "SELECT id, brandid, seriesid, `month`, cityid, created_stime, modified_stime FROM rank_sale_city_old ORDER BY id DESC LIMIT 1";

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
                                          @Param("endMonth") String endMonth,
                                          @Param("cityId") Integer cityId,
                                          @Param("size") Integer size) {
        StringBuilder sqlBuilder = new StringBuilder(BASE_SQL);
        // 如果起始月份和结束月份相同, 则使用 = 不同时使用BETWEEN
        if (StringUtils.hasLength(beginMonth) && beginMonth.equals(endMonth)) {
            sqlBuilder.append(" WHERE `month` = #{beginMonth}");
        } else {
            sqlBuilder.append(" WHERE `month` BETWEEN #{beginMonth} AND #{endMonth}");
        }
        if (cityId != 0) {
            sqlBuilder.append(" AND cityid = #{cityId}");
        }
        sqlBuilder.append(" GROUP BY seriesid")
                .append(" ORDER BY salecnt DESC,seriesid")
                .append(" LIMIT #{size}");

        return sqlBuilder.toString();
    }


    public String getLastOneByMonth() {
        return "SELECT seriesid, cityid, `month`, min_guidance_price, max_guidance_price, brandid, salecnt, manu_type, `level`, is_newenergy, created_stime, modified_stime, is_del FROM rank_sale_city_old WHERE `month`=#{month} ORDER BY id DESC LIMIT 1";
    }


    public String getAllCityByMonth(String month, Integer isNewEnergy) {
        return "SELECT brandid, SUM(salecnt) AS salecnt FROM rank_sale_city_old WHERE month = '" + month + "' " + (isNewEnergy == 1 ? "AND is_newenergy = 1" : StrPool.EMPTY) + " GROUP BY brandid";
    }
}
