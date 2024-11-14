package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CarSpecColorEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecColorListEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecColorMapper {

    @Select("SELECT A.seriesId,A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber\n" +
            " FROM innerSpecColor AS A WITH(NOLOCK)\n" +
            " INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            " WHERE B.SpecState>=10 AND  B.SpecState<=30 AND B.isshow=1 AND  B.parent=#{seriesId}")
    List<SpecColorListEntity> getOnSoldSpecInnerColorList(int seriesId);

    @Select("SELECT A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber,isnull(price,0) as price,remarks\n" +
            "FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            "WHERE B.SpecState>=20 AND  B.SpecState<=30 AND B.IsImageSpec=0 AND  B.parent=#{seriesId}\n" +
            "UNION ALL\n" +
            "SELECT A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber,isnull(price,0) as price,remarks\n" +
            "FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            "WHERE B.SpecState<=10 AND B.IsImageSpec=0  AND B.isshow=1 AND  B.parent=#{seriesId};")
    List<SpecColorListEntity> getOnSoldSpecSpecColorList(int seriesId);

    @Select("SELECT\n" +
            "\tA.SeriesId AS seriesId,\n" +
            "\tA.SpecId AS specId,\n" +
            "\tA.ColorId AS colorId,\n" +
            "\tB.PicNumber AS PicNumber,\n" +
            "\t0 ClubPicNumber,\n" +
            "\tprice,\n" +
            "\tremarks \n" +
            "FROM\n" +
            "\tinnerSpecColor AS A WITH ( NOLOCK )\n" +
            "\tLEFT JOIN ( SELECT SpecId, ColorId, SUM ( PicNumber ) AS PicNumber FROM CarSpecInnerColorStatistics WITH ( NOLOCK ) WHERE SpecId = #{specId} GROUP BY SpecId, ColorId ) B ON A.SpecId= B.SpecId \n" +
            "\tAND A.ColorId = B.ColorId \n" +
            "WHERE\n" +
            "\tA.SpecId = #{specId}")
    List<SpecColorListEntity> getSpecInnerColorBySpecId(int specId);

    @Select("SELECT\n" +
            "\tA.SeriesId AS seriesId,\n" +
            "\tA.SpecId AS specId,\n" +
            "\tA.ColorId AS colorId,\n" +
            "\tB.PicNumber AS PicNumber,\n" +
            "\t0 ClubPicNumber,\n" +
            "\tA.price,\n" +
            "\tA.remarks \n" +
            "FROM\n" +
            "\tCar_Spec_Color AS A WITH ( NOLOCK )\n" +
            "\tLEFT JOIN ( SELECT SpecId, ColorId, SUM ( PicNumber ) AS PicNumber FROM CarSpecPicColorStatistics WITH ( NOLOCK ) WHERE SpecId = #{specId} GROUP BY SpecId, ColorId ) B ON A.SpecId= B.SpecId \n" +
            "\tAND A.ColorId = B.ColorId \n" +
            "WHERE\n" +
            "\tA.SpecId = #{specId}")
    List<SpecColorListEntity> getSpecOuterColorBySpecId(int specId);

}
