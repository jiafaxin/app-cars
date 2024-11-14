package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.Car60PictureEntity;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.CarSixtyPointEntity;
import com.autohome.app.cars.mapper.popauto.providers.CarPhotoViewProvide;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface CarPhotoViewMapper {

    /**
     * 状态排序 未售+在售靠前 停售靠后
     * 类别排序 (id:1),中控方向盘(id:10),车厢座椅(id:3),其他细节(id:12),评测(id:13),改装(id:51),图解(id:14),活动(id:15) ,官图（id:53）
     * 20200218 类别排序改为： 外观(id:1),中控方向盘(id:10),车厢座椅(id:3),其他细节(id:12), 影·致(id:54),官图（id:53),评测(id:13),重要特点(id:14),改装(id:51),活动(id:15) ,
     * sourceTypeOrder 图片来源排序 为把经销商图片审核通过的图片排到编辑的图片后面，0 编辑上传、5 是经销商推图、10 是网友传图。（推送缺25图车型给经销商，经销商推图）
     *
     * @param seriesId
     */
    @Select("SELECT A.Id,A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,A.IsHD,A.isTitle,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,\n" +
            "CASE WHEN A.SpecState<=30 THEN 0 ELSE 1 END AS StateOrder,case IsClubPhoto when 3 then 0 when 2  then 0 else IsClubPhoto end as IsClubPhoto,\n" +
            "CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder,A.isclassic,A.dealerPicOrder,\n" +
            "CASE IsClubPhoto when 2 then 5 when 1 then 10 when 3 then 0 else IsClubPhoto end as sourceTypeOrder ,isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder ,width,height,dealerid\n" +
            ",isnull(pointlocatinid,0) as pointlocatinid,isnull(IsWallPaper,0) as IsWallPaper,isnull(optional,0) as optional, isnull(showId,0) as showId\n" +
            "FROM CarPhotoView AS A WITH(NOLOCK)\n" +
            "WHERE A.SeriesId=#{seriesId};")
    List<CarPhotoViewEntity> getPhotoViewBySeries(int seriesId);

    @Select("SELECT SpecId,SeriesId,PicClass,PicFilePath,PicId,PicColorId,IsHD,isTitle,SpecState,SyearId,Syear,SpecPicNumber,InnerColorId,isnull(showId,0) as showId,StateOrder,\n" +
            "                                IsClubPhoto,ClassOrder,isclassic,dealerPicOrder,sourceTypeOrder,SpecPicUploadTimeOrder,rn from (\n" +
            "\t                            SELECT  A.SpecId,A.SeriesId,A.PicClass,A.PicFilePath,A.PicId,A.PicColorId,A.IsHD,A.isTitle,A.SpecState,A.SyearId,A.Syear,A.SpecPicNumber,InnerColorId,showId,\n" +
            "\t                            CASE WHEN A.SpecState<=30 THEN 0 ELSE 1 END AS StateOrder,case IsClubPhoto when 3 then 0  when 2  then 0 else IsClubPhoto end as IsClubPhoto,\n" +
            "\t                            CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder,A.isclassic,A.dealerPicOrder,\n" +
            "\t                            CASE IsClubPhoto when 2 then 5 when 1 then 10  when 3 then 0 else IsClubPhoto end as sourceTypeOrder ,isnull(SpecPicUploadTimeOrder,0) as SpecPicUploadTimeOrder,row_number()over(partition by picclass order by picid desc) as rn\n" +
            "\t                            FROM CarPhotoView AS A WITH(NOLOCK)\n" +
            "                            WHERE A.SeriesId=#{seriesId} ) as T where rn <=10")
    List<CarPhotoViewEntity> getPhotoViewClassPicTop10BySeriesId(int seriesId);

    /**
     * 获取车系下的图片
     * 1外观>53官图>内饰(10中控+3座椅)>12细节
     */
    @SelectProvider(type = CarPhotoViewProvide.class, method = "getPhotoViewBySeriesIdSql")
    List<CarPhotoViewEntity> getPhotoViewBySeriesId(@Param("seriesId") int seriesId,@Param("publishTime") String publishTime);

    @Select("select Ordercls   from (\n" +
            "select  A.Ordercls,ROW_NUMBER() over(partition by A.Ordercls order by A.specid desc ) as rm  from Car60PictureView as A WITH(NOLOCK) inner join SpecView as B WITH(NOLOCK) on A.specid = B.specId\n" +
            "where B.SpecState>=20 and B.SpecState<=30 and seriesid=#{seriesId}\n" +
            ") as T where rm =2")
    List<Integer> getDeletgateSixtyPicSpecPointLocation(Integer seriesId);

    @Select(" select * from (\n" +
            "        select A.specId ,A.pointLocatinId ,A.picId ,A.picPath,A.ordercls,ROW_NUMBER ()over(PARTITION by A.SpecId,A.PointLocatinId order by A.picid DESC) as rn   from Car60PictureView as A with(nolock)\n" +
            "        inner join specview as B with(nolock) on A.specid=B.specId \n" +
            "        where B.seriesId = #{seriesId} and  B.SpecState >=20 and B.SpecState <=30\n" +
            "        ) as T where rn=1")
    List<Car60PictureEntity> getCar60PicViewBySeries(@Param("seriesId") Integer seriesId);

    /**
     * 获取60图点位信息
     *
     * @return
     */
    @Select("select pointid,pointname,ordercls from Car_Sixty_Point_Item as A with(nolock) order by Ordercls")
    List<CarSixtyPointEntity> getCarSixtyPointItem();
}
