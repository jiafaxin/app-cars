package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CarPhotoEntity;
import com.autohome.app.cars.mapper.popauto.entities.PicCountEntity;
import com.autohome.app.cars.mapper.popauto.entities.PicParamConfigEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("popauto")
public interface CarPhotoMapper {

    @Select("SELECT\n" +
            "    A.Id, A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,\n" +
            "    A.isclassic, A.dealerPicOrder, isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder ,width,height,dealerid,isnull(pointlocatinid,0) as pointlocatinid,\n" +
            "    isnull(IsWallPaper,0) as IsWallPaper, isnull(optional,0) as optional,  isnull(showId,0) as showId,isClubPhoto\n" +
            "FROM CarPhotoView AS A WITH(NOLOCK)\n" +
            "WHERE A.SeriesId= #{seriesId}")
    List<CarPhotoEntity> getAllPhotosBySeriesId(int seriesId);


    @Select("SELECT V.DataType,V.ItemId,ISNULL(I.Name,'') as name,V.valu as value,V.SubItemId,ISNULL(CSI.Name,'') as subName,V.SubItemOrderCls,V.PicId\n" +
            "FROM Visual_ParamconfigView V WITH(NOLOCK)\n" +
            "     left JOIN ConfigItem I WITH(NOLOCK) ON  V.DataType = 2 and V.ItemId = I.Id\n" +
            "     left JOIN ConfigSubItem CSI WITH(NOLOCK) ON V.DataType = 2 and V.SubItemId = CSI.Id\n" +
            "WHERE SeriesId=#{seriesId}\n" +
            "order by DataType asc,SubItemOrderCls desc")
    List<PicParamConfigEntity> getPicParamConfig(int seriesId);

    @Select("select distinct SeriesId from CarPhotoView_SyncLog with(nolock) where created_stime > DATEADD(minute, -10, GETDATE())")
    List<Integer> getUpdateSeriesIds();

    @Select("select 1   AS typeId,\n" +
            "       0          AS specId,\n" +
            "       PicColorId AS colorId,\n" +
            "       count(1)   AS picCount\n" +
            "from CarPhotoView with (nolock)\n" +
            "where SeriesId = #{seriesId}\n" +
            "  and PicClass in (1, 12)\n" +
            "group by PicColorId;\n ")
    List<PicCountEntity> getSeriesOuterPicCount(int seriesId);
    @Select("select 1   AS typeId,\n" +
            "       SpecId     AS specId,\n" +
            "       PicColorId AS colorId,\n" +
            "       count(1)   AS picCount\n" +
            "from CarPhotoView with (nolock)\n" +
            "where SeriesId = #{seriesId}\n" +
            "  and PicClass in (1, 12)\n" +
            "group by SpecId, PicColorId;\n ")
    List<PicCountEntity> getSpecOuterPicCount(int seriesId);

    @Select("select PicClass     AS typeId,\n" +
            "       0            AS specId,\n" +
            "       InnerColorId AS innerColorId,\n" +
            "       count(1)     AS picCount\n" +
            "from CarPhotoView with (nolock)\n" +
            "where SeriesId = #{seriesId}\n" +
            "  and PicClass in (3, 10)\n" +
            "group by PicClass, InnerColorId;\n ")
    List<PicCountEntity> getSeriesInnerPicCount(int seriesId);
    @Select("select PicClass     AS typeId,\n" +
            "       SpecId       AS specId,\n" +
            "       InnerColorId AS innerColorId,\n" +
            "       count(1)     AS picCount\n" +
            "from CarPhotoView with (nolock)\n" +
            "where SeriesId = #{seriesId}\n" +
            "  and PicClass in (3, 10)\n" +
            "group by PicClass, SpecId, InnerColorId;\n ")
    List<PicCountEntity> getSpecInnerPicCount(int seriesId);





}
