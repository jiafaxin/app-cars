package com.autohome.app.cars.mapper.popauto.providers;

import java.util.List;

public class SeriesPicProvider {

    final String getSeriesPicSql = "" +
            ";with spec as (select id as specId, specstate\n" +
            "              from spec_new with (nolock)\n" +
            "              union all\n" +
            "              select id as specId, specstate\n" +
            "              from cv_spec with (nolock))\n" +
            "select A.seriesId,B.name as picName, A.PicClass as picId, sum(PicNumber) as picCount\n" +
            "from (select A.SeriesId, A.specid, A.picclass, A.PicNumber\n" +
            "      from CarSpecPicClassStatistics as A with (nolock)\n" +
            "               inner join spec as B with (nolock) on A.SpecId = B.specid\n" +
            "      where SpecState <= 30\n" +
            "      union all\n" +
            "      select A.SeriesId, A.specid, A.picclass, A.PicNumber\n" +
            "      from CarStopSpecPicClassStatistics as A with (nolock)\n" +
            "               inner join spec as B with (nolock) on A.SpecId = B.specid\n" +
            "      where SpecState = 40) as A\n" +
            "         Inner join car_spec_photo_struct as B with (nolock) on A.picclass = B.id\n" +
            "where picclass < 200\n" +
            "group by A.seriesId,picclass, B.name";

    public String getSeriesPicAll() {
        return getSeriesPicSql;
    }

    public String getSeriesAutoShowPicCountAll(Integer autoShowId) {
        String sql = "SELECT AutohomeBrandId as brandId,seriesId,count(1) as picCount\n" +
                "FROM Exposition.dbo.show_cars WITH (NOLOCK)\n" +
                "where showid = #{autoShowId}\n" +
                "group by AutohomeBrandId,seriesid";
        return sql;
    }

    public String getSeriesPicTop5BySeriesId() {
        String sql = "SELECT SpecId,\n" +
                "       SeriesId,\n" +
                "       PicClass,\n" +
                "       PicFilePath,\n" +
                "       PicId,\n" +
                "       PicColorId,\n" +
                "       IsHD,\n" +
                "       isTitle,\n" +
                "       SpecState,\n" +
                "       SyearId,\n" +
                "       Syear,\n" +
                "       SpecPicNumber,\n" +
                "       InnerColorId,\n" +
                "       isnull(showId, 0) as showId,\n" +
                "       StateOrder,\n" +
                "       IsClubPhoto,\n" +
                "       ClassOrder,\n" +
                "       isclassic,\n" +
                "       dealerPicOrder,\n" +
                "       sourceTypeOrder,\n" +
                "       SpecPicUploadTimeOrder,\n" +
                "       rn\n" +
                "from (SELECT A.SpecId,\n" +
                "             A.SeriesId,\n" +
                "             A.PicClass,\n" +
                "             A.PicFilePath,\n" +
                "             A.PicId,\n" +
                "             A.PicColorId,\n" +
                "             A.IsHD,\n" +
                "             A.isTitle,\n" +
                "             A.SpecState,\n" +
                "             A.SyearId,\n" +
                "             A.Syear,\n" +
                "             A.SpecPicNumber,\n" +
                "             InnerColorId,\n" +
                "             showId,\n" +
                "             CASE WHEN A.SpecState <= 30 THEN 0 ELSE 1 END                                    AS StateOrder,\n" +
                "             case IsClubPhoto when 3 then 0 when 2 then 0 else IsClubPhoto end                as IsClubPhoto,\n" +
                "             CASE A.PicClass\n" +
                "                 WHEN 10 THEN 2\n" +
                "                 when 54 then 12.2\n" +
                "                 when 53 then 12.5\n" +
                "                 when 51 then 15\n" +
                "                 when 15 then 16\n" +
                "                 ELSE A.PicClass END                                                          AS ClassOrder,\n" +
                "             A.isclassic,\n" +
                "             A.dealerPicOrder,\n" +
                "             CASE IsClubPhoto when 2 then 5 when 1 then 10 when 3 then 0 else IsClubPhoto end as sourceTypeOrder,\n" +
                "             isnull(SpecPicUploadTimeOrder, 0)                                                as SpecPicUploadTimeOrder,\n" +
                "             row_number() over (partition by picclass order by picid desc)                    as rn\n" +
                "      FROM CarPhotoView AS A WITH (NOLOCK) " +
                "       WHERE A.SeriesId=#{seriesId}) as T\n" +
                "where rn <= 5";
        return sql;
    }
}
