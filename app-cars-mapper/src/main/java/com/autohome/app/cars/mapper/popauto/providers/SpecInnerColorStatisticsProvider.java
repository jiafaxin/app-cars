package com.autohome.app.cars.mapper.popauto.providers;

public class SpecInnerColorStatisticsProvider {

    public String getSpecInnerColorStatisticsBySeriesId(int seriesId){

        //3080以后的车系都没有网友传图了
        if(seriesId <= 3080){
            return String.format("WITH ClubPicInfo AS(\n" +
                    "\tSELECT SpecId,InnerColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
                    "      FROM CarPhotoView WITH(NOLOCK)\n" +
                    "     WHERE %s InnerColorId>0 and IsClubPhoto=1\n" +
                    "  GROUP BY SpecId,InnerColorId,PicClass\n" +
                    ")\n" +
                    "\n" +
                    "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,ISNULL(C.ClubPicNum,0) AS ClubPicNumber,\n" +
                    "\t   CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "  FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
                    "\t   INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
                    "\t   LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.InnerColorId AND A.PicClass=C.PicClass\n" +
                    " WHERE %s A.PicClass<200\n" +
                    "ORDER BY ClassOrder;",seriesId<=0?"":"SeriesId=${seriesId} and",seriesId<=0?"":"B.SeriesId=${seriesId} AND");
        }else{
            return "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,0 AS ClubPicNumber,\n" +
                    "\t   CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder\n" +
                    "  FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
                    "\t   INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId = B.SpecId\n" +
                    " WHERE B.SeriesId = ${seriesId} AND A.PicClass < 200\n" +
                    "ORDER BY ClassOrder; ";
        }
    }

    public String getAllSpecInnerColorStatisticsBySeriesId(){
//        return getSpecInnerColorStatisticsBySeriesId(0);
        return "WITH ClubPicInfo\n" +
                "AS( SELECT SpecId,InnerColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
                "    FROM CarPhotoView WITH(NOLOCK)\n" +
                "    WHERE SeriesId <= 3080 and InnerColorId>0 and IsClubPhoto=1\n" +
                "    GROUP BY SpecId,InnerColorId,PicClass)\n" +
                "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,ISNULL(C.ClubPicNum,0) AS ClubPicNumber,\n" +
                "\t\tCASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder  \n" +
                "FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
                "INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
                "LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.InnerColorId AND A.PicClass=C.PicClass\n" +
                "WHERE B.SeriesId <= 3080 AND A.PicClass<200\n" +
                "union all\n" +
                "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,0 AS ClubPicNumber,\n" +
                "        CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder\n" +
                "FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
                "INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId = B.SpecId\n" +
                "WHERE B.SeriesId > 3080 AND A.PicClass < 200\n" +
                "ORDER BY SeriesId, ClassOrder";
    }

}
