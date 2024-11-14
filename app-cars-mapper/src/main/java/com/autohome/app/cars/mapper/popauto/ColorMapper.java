package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.PicColorEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface ColorMapper {

    @Select("WITH ClubPicInfo AS( \n" +
            "    SELECT SpecId,PicColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
            "    FROM CarPhotoView WITH(NOLOCK)\n" +
            "    WHERE  PicColorId>0 and IsClubPhoto=1\n" +
            "    GROUP BY SpecId,PicColorId,PicClass\n" +
            ")\n" +
            "SELECT A.ColorId,D.ColorName,D.ColorValue,\n" +
            "       B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber\n" +
            "   , CASE WHEN B.SeriesId > 3080 THEN 0 ELSE ISNULL(C.ClubPicNum,0) END AS ClubPicNumber\n" +
            "   ,D.ColorName,D.ColorValue\n" +
            "FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\n" +
            " INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            " LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON A.SpecId=C.specId AND A.ColorId=C.PicColorId AND A.PicClass=C.PicClass\n" +
            " INNER JOIN Car_Fct_Color AS D WITH(NOLOCK ) ON D.Id = A.ColorId\n" +
            "WHERE A.PicClass<200")
    List<PicColorEntity> getAllColors();

    @Select("WITH ClubPicInfo AS(\n" +
            "    SELECT SpecId,InnerColorId,PicClass,COUNT(PicId) AS ClubPicNum\n" +
            "    FROM CarPhotoView WITH(NOLOCK)\n" +
            "    WHERE SeriesId <= 3080 and InnerColorId>0 and IsClubPhoto=1\n" +
            "    GROUP BY SpecId,InnerColorId,PicClass\n" +
            ")\n" +
            "SELECT A.ColorId,D.ColorName,D.ColorValue,\n" +
            "       B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.PicClass,A.PicNumber,ISNULL(C.ClubPicNum,0) AS ClubPicNumber\n" +
            "FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
            "INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            "LEFT  JOIN ClubPicInfo AS C WITH(NOLOCK) ON  A.SpecId=C.specId AND A.ColorId=C.InnerColorId AND A.PicClass=C.PicClass\n" +
            "INNER JOIN InnerFctColor AS D WITH(NOLOCK ) ON D.Id = A.ColorId\n" +
            "WHERE  A.PicClass<200")
    List<PicColorEntity> getAllInnerColors();

}
