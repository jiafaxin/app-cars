package com.autohome.app.cars.mapper.popauto.providers;

public class CrashTestSeriesProvider {

    private static final String itemIdStr1 = "(2,13,20,25)";

    private static final String itemIdStr2 = "(32,40,47,55)";

    public String getDataSql(int orderType, int standardId) {
        String withSql = getWithSql(standardId);

        String tempItemIdStr = standardId == 2 ? itemIdStr2 : itemIdStr1;
        switch (orderType) {
            case 1:
                return withSql + " select top 320 seriesid,publishdate,itemid,crashvalue,A.crashworthiness from alldata as A with(nolock) inner join crashtest_detail as B with(nolock)\n" +
                        "on A.crashtestid = B.parentid where  itemid in " + tempItemIdStr + "order by  convert(decimal(6, 2),crashworthiness)" + (standardId == 2 ? " asc" : " desc");
            case 2:
                return withSql + " select top 320 seriesid,publishdate,itemid,crashvalue,CASE WHEN A.passengersafety<1 THEN 100 ELSE A.passengersafety end as passengersafety from alldata as A with(nolock) inner join crashtest_detail as B with(nolock)\n" +
                        "on A.crashtestid = B.parentid where  itemid in " + tempItemIdStr + " order by passengersafety asc ";
            case 3:
                return withSql + " select top 320 seriesid,publishdate,itemid,crashvalue,A.passerbysafety from alldata as A with(nolock) inner join crashtest_detail as B with(nolock)\n" +
                        "on A.crashtestid = B.parentid where  itemid in " + tempItemIdStr + " order by  convert(decimal(6, 2),passerbysafety) desc ";
            case 4:
                return withSql + " select top 320 seriesid,publishdate,itemid,crashvalue,A.vehicleAuxiliarySafety from alldata as A with(nolock) inner join crashtest_detail as B with(nolock)\n" +
                        "on A.crashtestid = B.parentid where  itemid in " + tempItemIdStr + " order by convert(decimal(6, 2),vehicleAuxiliarySafety) desc ";
            default:
                return withSql + " select top 320 seriesid,publishdate,itemid,crashvalue,A.crashworthiness from alldata as A with(nolock) inner join crashtest_detail as B with(nolock)\n" +
                        " on A.crashtestid = B.parentid where  itemid in " + tempItemIdStr + " order by publishdate desc ";
        }

    }

    public String getCrashCnCapTestData() {
        return "WITH series_rank_cncap AS (\n" +
                "SELECT\n" +
                "id,\n" +
                "seriesid,\n" +
                "isnull( [12], 4 ) AS compscore,\n" +
                "isnull( [13], 4 ) AS starscore \n" +
                "FROM ( SELECT\n" +
                "A.id,\n" +
                "A.seriesid,\n" +
                "B.itemid,\n" +
                "B.crashvalue \n" +
                "FROM\n" +
                "crashtest_series AS A WITH ( nolock )\n" +
                "LEFT JOIN crashtest_cnacp_detail AS B ON A.id = B.parentid \n" +
                "WHERE\n" +
                "A.standard_id = 3 \n" +
                "AND A.publishstate = 10 \n" +
                "AND itemid IN ( 12, 13 ) \n" +
                ") AS T PIVOT ( MAX ( crashvalue ) FOR itemid IN ( [12], [13] ) ) AS T \n" +
                "\n" +
                ") \n" +
                "SELECT\n" +
                "src.*,\n" +
                "b.jb AS levelid \n" +
                "FROM\n" +
                "series_rank_cncap AS src\n" +
                "JOIN Brands b ON b.id= src.seriesid\n" +
                "order by src.compscore desc ";
    }

    private String getWithSql(int standardId) {
        return standardId == 2 ? getWithSql2() : getWithSql1();
    }


    private String getWithSql1() {
        return " WITH crashSeries AS (\n" +
                "SELECT * FROM(\n" +
                "SELECT\n" +
                "A.id AS crashtestid,\n" +
                "seriesid,\n" +
                "B.jb AS levelid,\n" +
                "update_time AS publishdate,\n" +
                "ROW_NUMBER ( ) OVER ( PARTITION BY seriesid ORDER BY A.ID DESC ) AS RN,\n" +
                "A.standard_id \n" +
                "FROM\n" +
                "crashtest_series AS A WITH ( NOLOCK )\n" +
                "INNER JOIN brands AS B ON A.seriesid = B.id \n" +
                "WHERE\n" +
                "A.standard_id = 1 \n" +
                "AND A.publishstate = 10 \n" +
                ") AS T \n" +
                "WHERE\n" +
                "RN = 1 \n" +
                "),\n" +
                "CrashResult AS (\n" +
                "SELECT\n" +
                "id,\n" +
                "seriesid,\n" +
                "isnull( NULLIF([3] ,''), 0 ) AS Crashworthiness,\n" +
                "isnull( NULLIF([13],''), 4 ) passengersafety,\n" +
                "ISNULL( NULLIF([29],''), 0 ) AS passerbysafety,\n" +
                "isnull( NULLIF([30],''), 0 ) AS vehicleAuxiliarySafety \n" +
                "FROM(\n" +
                "SELECT\n" +
                "A.id,\n" +
                "A.seriesid,\n" +
                "B.itemid,\n" +
                "B.crashvalue \n" +
                "FROM\n" +
                "crashtest_series AS A\n" +
                "LEFT JOIN crashtest_detail AS B ON A.id = B.parentid \n" +
                "WHERE\n" +
                "A.standard_id = 1 \n" +
                "AND A.publishstate = 10 \n" +
                "AND itemid IN ( 3, 13, 29, 30 ) \n" +
                ") AS T PIVOT ( MAX ( crashvalue ) FOR itemid IN ( [3], [13], [29], [30] ) ) AS T \n" +
                "),\n" +
                "alldata AS (\n" +
                "SELECT\n" +
                "A.crashtestid,\n" +
                "A.levelid,\n" +
                "A.publishdate,\n" +
                "A.seriesid,\n" +
                "B.Crashworthiness,\n" +
                "B.passengersafety,\n" +
                "B.passerbysafety,\n" +
                "B.vehicleAuxiliarySafety,\n" +
                "A.standard_id \n" +
                "FROM\n" +
                "crashSeries AS A\n" +
                "INNER JOIN CrashResult AS B ON A.crashtestid= B.id \n" +
                ") ";
    }

    private String getWithSql2() {
        return " WITH crashSeries AS (\n" +
                "SELECT * FROM(\n" +
                "SELECT\n" +
                "A.id AS crashtestid,\n" +
                "seriesid,\n" +
                "B.jb AS levelid,\n" +
                "update_time AS publishdate,\n" +
                "ROW_NUMBER ( ) OVER ( PARTITION BY seriesid ORDER BY A.ID DESC ) AS RN,\n" +
                "A.standard_id \n" +
                "FROM\n" +
                "crashtest_series AS A WITH ( NOLOCK )\n" +
                "INNER JOIN brands AS B ON A.seriesid = B.id \n" +
                "WHERE\n" +
                "A.standard_id = 2 \n" +
                "AND A.publishstate = 10 \n" +
                ") AS T \n" +
                "WHERE\n" +
                "RN = 1 \n" +
                "),\n" +
                "CrashResult AS (\n" +
                "SELECT\n" +
                "id,\n" +
                "seriesid,\n" +
                "isnull(nullif([32],''), 4 ) AS Crashworthiness,\n" +
                "isnull(nullif([40],''), 4 ) passengersafety,\n" +
                "ISNULL(nullif([48],''), 0 ) AS passerbysafety,\n" +
                "isnull(nullif([56],''), 0 ) AS vehicleAuxiliarySafety \n" +
                "FROM\n" +
                "(\n" +
                "SELECT\n" +
                "A.id,\n" +
                "A.seriesid,\n" +
                "B.itemid,\n" +
                "B.crashvalue \n" +
                "FROM\n" +
                "crashtest_series AS A\n" +
                "LEFT JOIN crashtest_detail AS B ON A.id = B.parentid \n" +
                "WHERE\n" +
                "A.standard_id = 2 \n" +
                "AND A.publishstate = 10 \n" +
                "AND itemid IN ( 32, 40, 48, 56 ) \n" +
                ") AS T PIVOT ( MAX ( crashvalue ) FOR itemid IN ( [32], [40], [48], [56] ) ) AS T \n" +
                "),\n" +
                "alldata AS (\n" +
                "SELECT\n" +
                "A.crashtestid,\n" +
                "A.levelid,\n" +
                "A.publishdate,\n" +
                "A.seriesid,\n" +
                "B.Crashworthiness,\n" +
                "B.passengersafety,\n" +
                "B.passerbysafety,\n" +
                "B.vehicleAuxiliarySafety,\n" +
                "A.standard_id \n" +
                "FROM\n" +
                "crashSeries AS A\n" +
                "INNER JOIN CrashResult AS B ON A.crashtestid= B.id ) ";
    }
}
