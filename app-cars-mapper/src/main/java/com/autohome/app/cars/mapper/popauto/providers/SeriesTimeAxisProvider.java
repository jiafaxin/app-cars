package com.autohome.app.cars.mapper.popauto.providers;

import org.apache.ibatis.annotations.Param;

/**
 * @author : zzli
 * @description : 车系日历sql
 * @date : 2024/4/26 10:41
 */
public class SeriesTimeAxisProvider {

    public String getSpecList() {
        return "select T.*,E.year as yearName from (\n" +
                "select  parent as seriesId ,id,model  as name ,TimeMarket,isshow as paramisshow,IsImageSpec,dtime,SpecState as state,spec_price as minPrice,spec_year as YearId from spec_new with(nolock)  \n" +
                "UNION ALL \n" +
                "select  seriesId,id,SpecName as name,TimeMarket,ParamShow as paramisshow,0 as IsImageSpec,dtime,SpecState as state ,minPrice,YearId from CV_Spec with(nolock))T\n" +
                "join spec_year (nolock) as E on T.SeriesId = E.series_id and T.YearId = E.id";
    }

    public String getPicFirstAddTime(int seriesId) {
        return "select top 1 PicClass,dtime from CarPhotoView WITH(NOLOCK) where seriesid = #{seriesId} and SpecPicNumber > 2 and PicClass in (1,10,3,12,14,53,55) order by dtime";
    }

    public String getPicUpdateTime(@Param("seriesId") Integer seriesId, @Param("outDate") String outDate) {
        return "select top 1 PicClass,dtime from CarPhotoView WITH(NOLOCK) where seriesid = #{seriesId} and SpecPicNumber > 2 and dtime>#{outDate} and PicClass in (1,10,3,12,14,53,55) order by dtime desc ";
    }

    String getParamSql() {
        return "WITH SPEC AS(\n" +
                "select id as specid from spec_new WITH(NOLOCK) where parent = #{seriesId}\n" +
                "union all\n" +
                "select id as specid from cv_spec WITH(NOLOCK) where seriesid = #{seriesId} \n" +
                "),ParamDate as (\n" +
                "\n" +
                "select a.specid,a.ParamId,a.Created_Stime from ParamSpecRelation as A WITH(NOLOCK) inner join SPEC as B WITH(NOLOCK) ON A.SpecId = b.specid\n" +
                "union all\n" +
                "select a.specid,a.ParamId,a.Created_Stime from ParamSpecSubItemValueRelation as A WITH(NOLOCK) inner join SPEC as B WITH(NOLOCK) ON A.SpecId = b.specid\n" +
                "union all\n" +
                "select a.specid,a.ParamId,a.Created_Stime from ParamSpecRelation_CV as A WITH(NOLOCK) inner join SPEC as B WITH(NOLOCK) ON A.SpecId = b.specid\n" +
                "union all\n" +
                "select a.specid,a.ParamId,a.Created_Stime from ParamSpecSubItemValueRelation_cv as A WITH(NOLOCK) inner join SPEC as B WITH(NOLOCK) ON A.SpecId = b.specid)\n";
    }

    public String getParamFirstTime(int seriesId) {
        String paramSql = getParamSql();
        paramSql += "select top 1 Created_Stime from ParamDate WITH(NOLOCK) order by Created_Stime";
        return paramSql;
    }

    public String getParamUpdateTime(@Param("seriesId") Integer seriesId, @Param("outDate") String outDate) {
        String paramSql = getParamSql();
        paramSql += "select top 1 Created_Stime from ParamDate WITH(NOLOCK) where Created_Stime>#{outDate} order by Created_Stime desc";
        return paramSql;
    }
}
