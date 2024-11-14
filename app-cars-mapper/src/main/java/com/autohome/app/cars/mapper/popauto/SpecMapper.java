package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.mapper.popauto.providers.SpecProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface SpecMapper {

    /**
     * 乘用车，车型信息；
     */
    @SelectProvider(value = SpecProvider.class, method = "getSpecAll")
    List<SpecEntity> getSpecAll();

    @Select("SELECT id FROM spec_new WITH(NOLOCK)")
    List<Integer> getAllSpecIds();

    @SelectProvider(value = SpecProvider.class, method = "getSpec")
    SpecEntity getSpec(int specId);

    @SelectProvider(value = SpecProvider.class, method = "getSpecBySeriesId")
    List<SpecEntity> getSpecBySeriesId(int seriesId);

    @SelectProvider(value = SpecProvider.class, method = "getSpecBySeriesIds")
    List<SpecEntity> getSpecBySeriesIds(List<Integer> seriesIds);

    /**
     * 商用车，车型信息
     */
    @SelectProvider(value = SpecProvider.class, method = "getCvSpecAll")
    List<SpecEntity> getCvSpecAll();

    @Select("SELECT id FROM CV_Spec WITH(NOLOCK)")
    List<Integer> getAllCvSpecIds();

    @SelectProvider(value = SpecProvider.class, method = "getCvSpec")
    SpecEntity getCvSpec(int specId);

    @SelectProvider(value = SpecProvider.class, method = "getCvSpecBySeriesId")
    List<SpecEntity> getCvSpecBySeriesId(int seriesId);
    @SelectProvider(value = SpecProvider.class,method = "getAllSpecBySeriesId")
    List<SpecViewEntity> getAllSpecBySeriesId(int seriesId, boolean isCV);

    @SelectProvider(value = SpecProvider.class,method = "isSpecElectric")
    Integer isSpecElectric(int specId);

    /**
     * 按续航查最大的车型（乘用车）
     * @param seriesId
     * @param driveRange
     * @return
     */
    @Select("select TOP(1) SpecId\n" +
            "from ParamSpecRelation as A with(nolock) inner join spec_new as B with(nolock) on A.SpecId =B.id  \n" +
            "where B.parent=#{seriesId} and A.ParamValue = #{driveRange} and ParamId in (101,135,75)\n" +
            "ORDER BY ParamId,SpecId")
    Integer getMaxSpecIdByXuhang(int seriesId,String driveRange);


    /**
     * 按续航查最大的车型（商用车）
     * @param seriesId
     * @param driveRange
     * @return
     */
    @Select("select TOP(1) SpecId\n" +
            "from ParamSpecRelation_CV  as A with(nolock) inner join CV_Spec  as B with(nolock) on A.SpecId =B.id  \n" +
            "where B.seriesid=#{seriesId} and A.ParamValue = #{driveRange} and ParamId in (101,135,75)\n" +
            "ORDER BY ParamId,SpecId")
    Integer getMaxSpecIdByXuhang_CV(int seriesId,String driveRange);


    @Select("select ParamId,ParamValue\n" +
            "from ParamSpecRelation as A with(nolock) inner join spec_new as B with(nolock) on A.SpecId =B.id  \n" +
            "where  ParamId in (101,135,75) and ParamValue <> '' and ParamValue <> '-'  and A.SpecId = #{specId}")
    List<SpecOfficialRangeEntity> getSpecOfficialRange(int specId);

    @Select("select ParamId,ParamValue\n" +
            "from ParamSpecRelation_CV  as A with(nolock) inner join CV_Spec  as B with(nolock) on A.SpecId =B.id  \n" +
            "where  ParamId in (101,135,75) and ParamValue <> '' and ParamValue <> '-'  and A.SpecId = ${specId}")
    List<SpecOfficialRangeEntity> getSpecOfficialRange_CV(int specId);

    @Select("select id,parent as seriesId,SpecState as specState from spec_new\n" +
            "union all\n" +
            "select id,seriesId,SpecState as specState  from cv_spec")
    List<SpecBaseInfoEntity> getAllSpecBaseInfo();

    @Select("SELECT specId, specName, batteryCapacity FROM SpecView WHERE specId = #{specId}")
    SpecViewEntity getSpecViewBatteryCapacity(Integer specId);

    @Select("select specId,PicId,PicPath,Ordercls,TopId as Type from Car25PictureView with(nolock) where PicId > 0 ")
    List<Car25PhotoEntity> getAllSpec25Photos();
}
