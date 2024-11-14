package com.autohome.app.cars.mapper.popauto.providers;

public class SpecPicClassStatisticsProvider {

    public String getSpecPicClassStatisticsBySeriesId(int seriesId) {
        String sqlStr = "";
        if (seriesId <= 3080) {
            sqlStr = "WITH ClubPicInfo AS(\n" +
                    "                                                    SELECT SpecId,PicClass,COUNT(PicId) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK)\n" +
                    "                                                    WHERE seriesid=#{seriesId} and IsClubPhoto=1\n" +
                    "                                                    GROUP BY SpecId,PicClass)\n" +
                    "\n" +
                    "                                                SELECT A.SeriesId,A.SpecId,A.SyearId,A.Syear,A.SpecState,A.PicClass,A.PicNumber,A.ClassOrder,ISNULL(B.ClubPicNum,0) AS ClubPicNumber\n" +
                    "                                                FROM(\t\n" +
                    "\n" +
                    "\t\t                                                SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t                                                CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t                                                FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join SpecView as B with(Nolock) on A.SpecId = B.specId where A.SeriesId=#{seriesId} and SpecState<=30\n" +
                    "\t\t                                                UNION ALL\n" +
                    "\t\t                                                SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t                                                CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16  ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t                                                FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  SpecView as B with(Nolock) on A.SpecId = B.specId  where A.SeriesId =#{seriesId}  and B.SpecState=40\n" +
                    "\t\t\t\t\t\t\t\t\n" +
                    "                                                ) AS A\n" +
                    "                                                LEFT JOIN ClubPicInfo AS B WITH(NOLOCK) ON A.specId=B.SpecId AND A.PicClass=B.PicClass\n" +
                    "                                                WHERE A.PicClass<200\n" +
                    "                                                ORDER BY ClassOrder;\n" +
                    "                                         --car.api.autohome.com.cn/app_code/Invoke/carpic/PictureView.cs;";
        } else {
            sqlStr = "SELECT A.SeriesId,A.SpecId,A.SyearId,A.Syear,A.SpecState,A.PicClass,A.PicNumber,A.ClassOrder,0 AS ClubPicNumber\n" +
                    "                                                FROM(\t\n" +
                    "\n" +
                    "\t\t                                                SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t                                                CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16     ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t                                                FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join SpecView as B with(Nolock) on A.SpecId = B.specId where A.SeriesId=#{seriesId} and SpecState<=30\n" +
                    "\t\t                                                UNION ALL\n" +
                    "\t\t                                                SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t                                                CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16     ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t                                                FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  SpecView as B with(Nolock) on A.SpecId = B.specId  where A.SeriesId =#{seriesId}  and B.SpecState=40\n" +
                    "\t\t\t\t\t\t\t\t\n" +
                    "                                                ) AS A\n" +
                    "                                                WHERE A.PicClass<200\n" +
                    "                                                ORDER BY ClassOrder; --car.api.autohome.com.cn / app_code / Invoke / carpic / PictureView.cs; ";
        }
        return sqlStr;
    }

    public String getCVSpecPicClassStatisticsBySeriesId(int seriesId) {
        String sqlStr = "";
        if (seriesId <= 3080) {
            sqlStr = "WITH ClubPicInfo AS(\n" +
                    "                                                        SELECT SpecId,PicClass,COUNT(PicId) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK)\n" +
                    "                                                        WHERE seriesid=#{seriesId} and IsClubPhoto=1\n" +
                    "                                                        GROUP BY SpecId,PicClass)\n" +
                    "\n" +
                    "                                                    SELECT A.SeriesId,A.SpecId,A.SyearId,A.Syear,A.SpecState,A.PicClass,A.PicNumber,A.ClassOrder,ISNULL(B.ClubPicNum,0) AS ClubPicNumber\n" +
                    "                                                    FROM(\t\n" +
                    "\t\t\t                                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t\t                                                    CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t\t                                                    FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join CV_SpecView as B with(Nolock) on A.SpecId = B.specId where A.SeriesId=#{seriesId} and SpecState<=30\n" +
                    "\t\t\t                                                    UNION ALL\n" +
                    "\t\t\t                                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t\t                                                    CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t\t                                                    FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  CV_SpecView  as B with(Nolock) on A.SpecId = B.specId  where A.SeriesId =#{seriesId}  and B.SpecState=40\n" +
                    "\t\t\t\t\t\t\t\t\n" +
                    "                                                    ) AS A\n" +
                    "                                                    LEFT JOIN ClubPicInfo AS B WITH(NOLOCK) ON A.specId=B.SpecId AND A.PicClass=B.PicClass\n" +
                    "                                                    WHERE A.PicClass<200\n" +
                    "                                                    ORDER BY ClassOrder;\n" +
                    "                                                      --car.api.autohome.com.cn / app_code / Invoke / carpic / PictureView.cs; ";
        } else {
            sqlStr = "SELECT A.SeriesId,A.SpecId,A.SyearId,A.Syear,A.SpecState,A.PicClass,A.PicNumber,A.ClassOrder,0 AS ClubPicNumber\n" +
                    "                                                    FROM(\t\n" +
                    "\t\t\t                                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t\t                                                    CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16   ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t\t                                                    FROM CarSpecPicClassStatistics AS A WITH(NOLOCK) inner join CV_SpecView as B with(Nolock) on A.SpecId = B.specId where A.SeriesId=#{seriesId} and SpecState<=30\n" +
                    "\t\t\t                                                    UNION ALL\n" +
                    "\t\t\t                                                    SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,\n" +
                    "\t\t\t                                                    CASE A.PicClass WHEN 10 THEN 2  WHEN 54 THEN 12.2 WHEN 53 THEN 12.5 WHEN 51 THEN 15  WHEN 15 THEN 16    ELSE A.PicClass END AS ClassOrder  \n" +
                    "\t\t\t                                                    FROM CarStopSpecPicClassStatistics AS A WITH(NOLOCK) inner join  CV_SpecView  as B with(Nolock) on A.SpecId = B.specId  where A.SeriesId =#{seriesId}  and B.SpecState=40\n" +
                    "\t\t\t\t\t\t\t\t\n" +
                    "                                                    ) AS A\n" +
                    "                                                    WHERE A.PicClass<200\n" +
                    "                                                    ORDER BY ClassOrder;\n" +
                    "                                                      --car.api.autohome.com.cn / app_code / Invoke / carpic / PictureView.cs; ";

        }
        return sqlStr;
    }
}
