package com.autohome.app.cars.mapper.popauto.providers;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

public class CarPhotoViewProvide {

    public String getPhotoViewBySeriesIdSql(@Param("seriesId") int seriesId, @Param("publishTime") String publishTime) {
        String sql = "SELECT A.Id,A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,B.ColorName,A.IsHD,A.isTitle,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,A.Dtime as PicUploadTime,\n" +
                "CASE WHEN A.SpecState<=30 THEN 0 ELSE 1 END AS StateOrder,case IsClubPhoto when 3 then 0 when 2  then 0 else IsClubPhoto end as IsClubPhoto,\n" +
                "CASE A.PicClass WHEN 10 THEN 2  when 12 then 3.2 when 53 then 1.2 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder,A.isclassic,A.dealerPicOrder,\n" +
                "CASE IsClubPhoto when 2 then 5 when 1 then 10 when 3 then 0 else IsClubPhoto end as sourceTypeOrder ,isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder ,width,height,dealerid\n" +
                ",isnull(pointlocatinid,0) as pointlocatinid,isnull(IsWallPaper,0) as IsWallPaper,isnull(optional,0) as optional, isnull(showId,0) as showId\n" +
                "FROM CarPhotoView AS A WITH(NOLOCK) \n" +
                "left join Car_Fct_Color AS B WITH(NOLOCK) on A.PicColorId=B.Id  \n" +
                "WHERE A.SeriesId=#{seriesId} ";
        if (StringUtils.isNotEmpty(publishTime)) {
            sql += " AND A.Dtime > #{publishTime} ";
        }
        return sql;
    }
}
