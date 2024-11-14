package com.autohome.app.cars.mapper.popauto.providers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class SeriesProvider {

    public String getAllSeries() {
        String sql = "" +
                "SELECT A.id, \n" +
                "    A.name, \n" +
                "    A.jb AS levelId, \n" +
                "    E.name AS levelName, \n" +
                "    A.img AS img, \n" +
                "    A.nobgcolorpicurl AS nobgimg, \n" +
                "    A.IsNewenergy, \n" +
                "    A.dtime  AS createTime, \n" +
                "    A.ispublic, \n" +
                "    A.isimport, \n" +
                "    A.Place, \n" +
                "    D.SeriesState as state, \n" +
                "    A.FirstLetter, \n" +
                "    A.delegate25SpecId, \n" +
                "    A.newenergy_SeriesId as newenergySeriesId, \n" +
                "    A.diffconfigisshow, \n" +
                "    A.pricedescription, \n" +
                "    D.SeriesNewRank, \n" +
                "    D.seriesPriceMin, \n" +
                "    D.seriesPriceMax, \n" +
                "    B.id AS brandId, \n" +
                "    B.img AS brandLogo, \n" +
                "    B.name AS brandName, \n" +
                "    C.id AS manufactoryId, \n" +
                "    C.name AS manufactoryName \n" +
                "FROM Brands AS A WITH(NOLOCK)\n" +
                "    JOIN [group] AS B WITH(NOLOCK) on A.newFctid = B.id \n" +
                "    JOIN Manufactory AS C WITH(NOLOCK) on A.M = C.id \n" +
                "    LEFT JOIN SeriesView AS D WITH(NOLOCK) on A.id = D.seriesId \n" +
                "    LEFT JOIN car_spec_jb AS E WITH(NOLOCK) on A.jb = E.id  \n";
        return sql;
    }

    public String getSeries(int seriesId) {
        String sql = getAllSeries();
        sql += " WHERE A.id = #{seriesId}";
        return sql;
    }

    public String getSeriesList(List<Integer> seriesIds) {
        String sql = getAllSeries();
        sql += " WHERE A.id in (<foreach collection=\"seriesIds\" item=\"__item\" separator=\",\" >#{__item}</foreach>)";
        return "<script>" + sql + "</script>";
    }
    public String getgetAllEnergySeries(){
        String sql = getAllSeries();
        sql += " WHERE A.IsNewenergy=1";
        return sql;
    }

    final static String seriesHasBookSpecSql = "SELECT DISTINCT sereisId, booked\n" +
            "FROM (\n" +
            "\tSELECT parent AS sereisId,booked FROM spec_new WHERE booked = 1 %s\n" +
            "    UNION\n" +
            "    SELECT SeriesId AS sereisId, booked FROM CV_Spec WHERE booked = 1 %s\n" +
            ") AS T";

    public String getSeriesHasBookSpec(int seriesId) {
        return String.format(seriesHasBookSpecSql, "AND parent = #{seriesId}", "AND SeriesId =  #{seriesId}");
    }


    public String getSeriesHasBookSpecAll() {
        return String.format(seriesHasBookSpecSql, " ", " ");
    }


    final static String getSeriesFuelTypeDetailSql =
            "SELECT DISTINCT *\n" +
                    "FROM(\n" +
                    "\tSELECT C.seriesId AS seriesId, A.SubParamId ,B.SubParamName\n" +
                    "\tFROM ParamSpecSubItemValueRelation AS A WITH(NOLOCK)\n" +
                    "\t\t INNER JOIN ParamSubItem AS B WITH(NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                    "\t\t JOIN SpecView AS C ON A.SpecId = C.specId %s\n" +
                    "\tWHERE A.paramid = 55\n" +
                    "\tUNION\n" +
                    "\tSELECT C.SeriesId AS seriesId, A.SubParamId ,B.SubParamName\n" +
                    "\tFROM ParamSpecSubItemValueRelation_CV AS A WITH(NOLOCK)\n" +
                    "\t\t INNER JOIN ParamSubItem AS B WITH(NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                    "\t\t JOIN CV_Spec AS C ON A.SpecId = C.id %s\n" +
                    "\tWHERE A.paramid = 55\n" +
                    ") AS T";

    public String getSeriesFuelTypeDetail() {
        return String.format(getSeriesFuelTypeDetailSql, " AND C.SeriesId=#{seriesId} ", " AND C.SeriesId=#{seriesId} ");
    }

    public String getSeriesFuelTypeDetailAll() {
        return String.format(getSeriesFuelTypeDetailSql, "", "");
    }

    final String getSeriesParamIsShowSql =
            "SELECT DISTINCT parent AS SeriesId\n" +
                    "FROM spec_new WITH (NOLOCK)\n" +
                    "WHERE IsImageSpec = 0 \n" +
                    "  AND isshow = 1 %s\n" +
                    "UNION ALL\n" +
                    "SELECT DISTINCT SeriesId\n" +
                    "FROM CV_SpecView WITH (NOLOCK)\n" +
                    "WHERE SpecIsShow = 1 %s";

    public String getSeriesParamIsShow() {
        return String.format(getSeriesParamIsShowSql, " AND parent=#{seriesId} ", " AND SeriesId=#{seriesId} ");
    }

    public String getSeriesParamIsShowAll() {
        return String.format(getSeriesParamIsShowSql, "", "");
    }


    final String getAllStopSeriesIsImageSpec =
            "select seriesid\n" +
                    "from (select COUNT(*)                                       as specSum,\n" +
                    "             SUM(case isimagespec when 1 then 1 else 0 end) as imgspecnum,\n" +
                    "             SUM(case SpecState when 40 then 1 else 0 end)  as stopSpecNum,\n" +
                    "             parent                                         as seriesid\n" +
                    "      from spec_new with (nolock) %s\n" +
                    "      group by parent)\n" +
                    "         as TT\n" +
                    "where specSum = imgspecnum\n" +
                    "  and specSum = stopSpecNum ";

    public String getAllStopSeriesIsImageSpec() {
        return String.format(getAllStopSeriesIsImageSpec, "");
    }

    public String getStopSeriesIsImageSpec() {
        return String.format(getAllStopSeriesIsImageSpec, " where parent=#{seriesId} ");
    }

    public String getVideoOrderBySeriesId(int seriesId) {
        String sql = "SELECT *\n" +
                "FROM(\n" +
                "\tselect A.orderId,B.taskId,B.brandId,B.seriesId,B.specId,(select count(1) from EpibolyAiVideoOrderDetail as C where status = 1 and is_del = 0 and C.orderId = A.orderId) as itemCount,\n" +
                "\tROW_NUMBER() OVER( PARTITION BY B.seriesId ORDER BY A.created_stime DESC) AS ROWNO\n" +
                "\tfrom EpibolyAiVideoOrder as A\n" +
                "\tjoin EpibolyAiVideoTask as B on A.taskId = B.taskId\n" +
                "\twhere A.is_del = 0\n" +
                "\t  and B.is_del = 0\n" +
                "\t  and A.orderStatus = 2\n" +
                ") AS T WHERE ROWNO = 1";
        if (seriesId <= 0) {
            return sql;
        } else {
            return sql + " and seriesId=#{seriesId}";
        }
    }
}
