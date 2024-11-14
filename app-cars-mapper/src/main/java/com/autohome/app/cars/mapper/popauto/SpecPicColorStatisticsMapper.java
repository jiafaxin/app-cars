package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.app.cars.mapper.popauto.providers.SpecPicColorStatisticsProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecPicColorStatisticsMapper {

    @SelectProvider(value = SpecPicColorStatisticsProvider.class,method = "GetSpecPicColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> GetSpecPicColorStatisticsBySeriesId(int seriesId);

    @Select("WITH ClubPicInfo AS( \n" +
            "   SELECT SpecId,PicColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
            "     FROM CarPhotoView WITH(NOLOCK)\n" +
            "    WHERE  PicColorId>0 and IsClubPhoto=1\n" +
            "  GROUP BY SpecId,PicColorId,PicClass\n" +
            ")\n" +
            "SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber,CASE WHEN B.SeriesId > 3080 THEN 0 ELSE ISNULL(C.ClubPicNum,0) END AS ClubPicNumber,\n" +
            "      CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16 ELSE A.PicClass END AS ClassOrder  \n" +
            "  FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\n" +
            "      INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            "      LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.PicColorId AND A.PicClass=C.PicClass\n" +
            " WHERE A.PicClass<200\n" +
            "ORDER BY ClassOrder;\n")
    List<SpecPicColorStatisticsEntity> getAllSpecPicColorStatisticsBySeriesId();
}
