package com.autohome.app.cars.mapper.popauto.providers;

public class SpecPicColorStatisticsProvider {

    public String GetSpecPicColorStatisticsBySeriesId(int seriesId){

        //3080以后的车系都没有网友传图了
        if(seriesId <= 3080){
            return "" +
                    "WITH ClubPicInfo AS( \n" +
                    "   SELECT SpecId,PicColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
                    "     FROM CarPhotoView WITH(NOLOCK)\n" +
                    "    WHERE SeriesId=#{seriesId} and PicColorId>0 and IsClubPhoto=1\n" +
                    "  GROUP BY SpecId,PicColorId,PicClass\n" +
                    ")\n" +
                    "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,ISNULL(C.ClubPicNum,0) AS ClubPicNumber,\n" +
                    "      CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16 ELSE A.PicClass END AS ClassOrder  \n" +
                    "  FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\n" +
                    "      INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
                    "      LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.PicColorId AND A.PicClass=C.PicClass\n" +
                    " WHERE B.SeriesId=#{seriesId} AND A.PicClass<200\n" +
                    "ORDER BY ClassOrder;\n";
        }else{
            return "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,0 AS ClubPicNumber,            \n" +
                    "      CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder        \n" +
                    "  FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)            \n" +
                    "      INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
                    " WHERE B.SeriesId=#{seriesId} AND A.PicClass<200\n" +
                    "ORDER BY ClassOrder;";
        }
    }

}
