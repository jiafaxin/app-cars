package com.autohome.app.cars.mapper.popauto.providers;

import com.autohome.app.cars.common.carconfig.Spec;

/**
 * @author : zzli
 * @description : 配置
 * @date : 2024/5/14 20:28
 */
public class SpecConfigProvider {

    public String getSpecStardConfigSql(String specIds) {
        return "WITH specconfiginfo AS\n" +
                "(\n" +
                "SELECT A.specid,A.ItemId,A.ItemValueId as itemvalue,0 AS subitemid,0 AS subitemvalue FROM ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE  A.specid in (" + specIds + ") and A.ItemValueId=1 \n" +
                "UNION ALL\n" +
                "SELECT A.specid,A.ItemId,0 as itemvalue,C.SubItemId,C.Value AS subitemvalue FROM  ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "INNER JOIN ConfigSubItemValueRelation as  C WITH(NOLOCK) ON A.ItemValueId  = C.ItemValueId \n" +
                "WHERE A.specid in (" + specIds + ")  and C.Value=1 \n" +
                "UNION ALL  \n" +
                "SELECT A.specid,A.ItemId,0 AS itemvalue, A.SubItemId,A.SubValue AS subitemvalue FROM ConfigSubItemSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE A.specid in (" + specIds + ")   and A.SubValue=1 \n" +
                "),\n" +
                "specconfigpic as ( \n" +
                " SELECT A.specid,A.itemid,A.subitemid,B.FilePath as picurl  FROM ConfigItem_RelationSpecPic AS A WITH(NOLOCK) \n" +
                " INNER JOIN car_spec_photo AS B WITH(NOLOCK)  ON A.picid = B.id WHERE A.specid in (" + specIds + ")  AND A.is_del=0 \n" +
                ")\n" +
                "SELECT A.SpecId,B.TypeId AS typeid,D.name as typename,D.sort as typesort,B.Sort AS itemorder,A.ItemId,cast(A.itemvalue as nvarchar) as itemvalue ,A.subitemid,A.subitemvalue, C.picurl,B.Name as itemname,B.logo as itemlogo,E.Name as subitemname,E.logo as subitemlogo,E.alias as subitemalias FROM\n" +
                "specconfiginfo AS A WITH(NOLOCK) INNER JOIN ConfigItem AS B WITH(NOLOCK)  ON A.ItemId = B.Id\n" +
                "inner join configtype as D with(nolock) on B.typeId = D.id\n" +
                "left join specconfigpic as C WITH(NOLOCK) on A.ItemId=C.itemId and A.subitemid=C.subitemid and A.SpecId=C.specid\n" +
                "left join ConfigSubItem as E with(nolock) on A.ItemId=E.ItemId and A.subitemid=E.Id\n" +
                "WHERE B.IsShow = 1";
    }

    public String getSpecConfigRelations(int specId){
        if(Spec.isCvSpec(specId)){
            return "SELECT SpecId,ItemId,ItemValueId as valueId FROM CV_ConfigSpecRelation WITH(NOLOCK) WHERE SpecId = #{specId}";
        }else{
            return "SELECT SpecId,ItemId,ItemValueId as valueId FROM ConfigSpecRelation WITH(NOLOCK) WHERE SpecId = #{specId}";
        }
    }

    public String getSpecConfigSubItems(int specId){
        if(Spec.isCvSpec(specId)){
            return "select SpecId,A.ItemId,A.SubItemId,A.SubValue,B.logo from CV_ConfigSubItemSpecRelation  as A with(nolock) inner join ConfigSubItem as B with(Nolock) on A.SubItemId = B.Id where SpecId = #{specId} order by B.Sort;";
        }else{
            return "select SpecId,A.ItemId,A.SubItemId,A.SubValue,B.logo from ConfigSubItemSpecRelation  as A with(nolock) inner join ConfigSubItem as B with(Nolock) on A.SubItemId = B.Id where SpecId = #{specId} order by B.Sort;";
        }
    }
}
