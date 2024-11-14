package com.autohome.app.cars.mapper.appcars.providers;

import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.mapper.appcars.entities.SeriesSubscribeNewsEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchengtao
 * @date 2024/8/13 11:07
 */
public class SeriesSubscribeNewsProvider {
    /**
     * 临时替换
     */
    public String selectLastAllType_v57(Integer seriesId) {

        return "SELECT ssn.series_id,\n" +
                "       ssn.biz_type,\n" +
                "       ssn.spec_id,\n" +
                "       ssn.city_id,\n" +
                "       ssn.is_show,\n" +
                "       ssn.data,\n" +
                "       ssn.display_time\n" +
                "FROM (\n" +
                "    SELECT sn.series_id,\n" +
                "           sn.biz_type,\n" +
                "           sn.spec_id,\n" +
                "           sn.city_id,\n" +
                "           sn.is_show,\n" +
                "           sn.data,\n" +
                "           sn.display_time,\n" +
                "           @rn := IF(@prev = CONCAT(sn.series_id, sn.spec_id, sn.biz_type, sn.city_id), @rn + 1, 1) AS rn,\n" +
                "           @prev := CONCAT(sn.series_id, sn.spec_id, sn.biz_type, sn.city_id)\n" +
                "    FROM series_subscribe_news sn \n" +
                "    CROSS JOIN (SELECT @rn := 0, @prev := '') r\n" +
                "    WHERE sn.series_id=" + seriesId + " \n" +
                "    ORDER BY sn.series_id, sn.spec_id, sn.biz_type, sn.city_id, sn.display_time DESC\n" +
                ") ssn\n" +
                "WHERE ssn.rn = 1";
    }

    public String selectLastAllType(Integer seriesId) {
        return "SELECT ssn.series_id,\n" +
                "       ssn.biz_type,\n" +
                "       ssn.spec_id,\n" +
                "       ssn.city_id,\n" +
                "       ssn.is_show,\n" +
                "       ssn.data\n" +
                "FROM (SELECT sn.series_id,\n" +
                "             sn.biz_type,\n" +
                "             sn.spec_id,\n" +
                "             sn.city_id,\n" +
                "             sn.is_show,\n" +
                "             sn.data,\n" +
                "             ROW_NUMBER() OVER (PARTITION BY series_id, spec_id, biz_type, city_id ORDER BY modified_stime DESC) AS rn\n" +
                "      FROM series_subscribe_news sn WHERE sn.series_id=" + seriesId + ") ssn\n" +
                "WHERE ssn.rn = 1";
    }

    public String saveAll(List<SeriesSubscribeNewsEntity> list) {
        List<String> valueList = new ArrayList<>(list.size());
        list.forEach(entity -> valueList.add("(" + entity.getSeries_id() + "," + entity.getSpec_id() + "," + entity.getCity_id() + "," +
                entity.getBiz_type() + "," + entity.getIs_show() + ",'" + entity.getData() + "')"));

        String insertSql = "INSERT INTO series_subscribe_news (series_id, spec_id, city_id, biz_type, is_show, data) VALUES ";
        StringBuilder sqlBuilder = new StringBuilder(insertSql);
        sqlBuilder.append(String.join(",", valueList));
        sqlBuilder.append(" modified_stime = now(), is_del=0");
        System.out.println(sqlBuilder);
        return sqlBuilder.toString();
    }

    public String pageGetListSql(int cityId, List<Integer> seriesIds, List<Integer> specIds, int pageSize, String searchAfterRequest) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT series_id,spec_id,biz_type,`data`,display_time FROM series_subscribe_news WHERE is_show = 1 ");
        sqlBuilder.append("AND city_id IN (0, " + cityId + ") ");

        String seriesIdsStr = seriesIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String specIdsStr = specIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        if (StringUtils.isNotEmpty(seriesIdsStr) && StringUtils.isNotEmpty(specIdsStr)) {
            sqlBuilder.append("AND ((series_id IN (" + seriesIdsStr + ") AND spec_id = 0)OR spec_id IN (" + specIdsStr + ")) ");
        } else if (StringUtils.isNotEmpty(seriesIdsStr) && StringUtils.isEmpty(specIdsStr)) {
            sqlBuilder.append("AND (series_id IN (" + seriesIdsStr + ") AND spec_id = 0) ");
        } else if (StringUtils.isEmpty(seriesIdsStr) && StringUtils.isNotEmpty(specIdsStr)) {
            sqlBuilder.append("AND spec_id IN (" + specIdsStr + ") ");
        }
        if (StringUtils.isNotEmpty(searchAfterRequest)) {
            sqlBuilder.append("AND display_time <'" + searchAfterRequest + "' ");
        }
        sqlBuilder.append("ORDER BY display_time DESC ");
        sqlBuilder.append("LIMIT " + pageSize);
        return sqlBuilder.toString();
    }

    public String getUpdatedCountByTimeRangeSql(int cityId, List<Integer> seriesIds, List<Integer> specIds, String date) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT count(1) FROM series_subscribe_news WHERE is_show = 1 ");
        sqlBuilder.append("AND city_id IN (0, " + cityId + ") ");

        String seriesIdsStr = seriesIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String specIdsStr = specIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        if (StringUtils.isNotEmpty(seriesIdsStr) && StringUtils.isNotEmpty(specIdsStr)) {
            sqlBuilder.append("AND ((series_id IN (" + seriesIdsStr + ") AND spec_id = 0)OR spec_id IN (" + specIdsStr + ")) ");
        } else if (StringUtils.isNotEmpty(seriesIdsStr) && StringUtils.isEmpty(specIdsStr)) {
            sqlBuilder.append("AND (series_id IN (" + seriesIdsStr + ") AND spec_id = 0) ");
        } else if (StringUtils.isEmpty(seriesIdsStr) && StringUtils.isNotEmpty(specIdsStr)) {
            sqlBuilder.append("AND spec_id IN (" + specIdsStr + ") ");
        }
        if (StringUtils.isNotEmpty(date)) {
            sqlBuilder.append("AND display_time <'" + date + "' ");
        }
        return sqlBuilder.toString();
    }

    public String getSubscribeSeriesListSql(List<Integer> seriesIdList, List<Integer> specIdList, int cityid) {
        String seriesIdsStr = seriesIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        String specIdsStr = specIdList.isEmpty() ? StrPool.EMPTY : StrPool.COMMA + specIdList.stream().map(String::valueOf).collect(Collectors.joining(StrPool.COMMA));
        return "SELECT series_id, MAX(display_time) AS display_time " +
                "FROM series_subscribe_news " +
                "WHERE " +
                "(series_id IN (" + seriesIdsStr + ") OR spec_id IN (-1" + specIdsStr + ")) " +
                "AND city_id IN (0, " + cityid + ") " +
                "AND is_show = 1 GROUP BY series_id";
    }
}
