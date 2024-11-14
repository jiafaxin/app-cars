package com.autohome.app.cars.mapper.popauto.providers;

/**
 * @author : zzli
 * @description : 车型亮点
 * @date : 2024/3/5 18:13
 */
public class SpecHighlightProvider {


    public String getSpecStandardConfigAndRelationPicListSql(Integer specId) {
        String sql = "WITH specconfiginfo AS\n" +
                "(\n" +
                "SELECT A.specid,A.ItemId,A.ItemValueId as itemvalue,0 AS subitemid,0 AS subitemvalue FROM ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE  A.specid=#{specId} and A.ItemValueId=1 and A.ItemId in(1, 2, 3, 4, 6, 8, 12, 15, 18, 19, 20, 21, 23, 24, 26, 30, 34, 39, 40, 42, 45, 47, 54, 63, 64, 77, 78, 79, 80, 83, 86, 93, 95, 100, 101, 102, 104, 105, 106, 107, 108, 109, 110, 111, 112, 115, 117, 118, 121, 127, 130, 131, 133, 134, 142, 144, 146, 147, 148, 149, 150, 151, 152, 153, 156, 159, 163, 164, 165, 166, 167, 168, 169, 170, 173, 174, 178, 179, 182, 183, 184, 185, 187, 189, 191, 193, 194, 195, 196, 197, 198, 199, 200, 202, 203, 205, 206, 207, 208, 209, 210, 211, 212, 213, 218, 219, 220, 221, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 247, 248, 249, 250, 251, 252, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 266, 267, 268, 269, 270, 272, 273, 274, 275, 276, 278, 279, 280, 281, 282, 283, 285, 286, 288, 289, 290, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 310, 311, 312, 313, 314, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341)\n" +
                "UNION ALL\n" +
                "SELECT A.specid,A.ItemId,0 as itemvalue,C.SubItemId,C.Value AS subitemvalue FROM  ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "INNER JOIN ConfigSubItemValueRelation as  C WITH(NOLOCK) ON A.ItemValueId  = C.ItemValueId \n" +
                "WHERE A.specid=#{specId}  and C.Value=1 and C.SubItemId in (19, 20, 196, 199, 202, 207, 210, 250, 329, 330, 331, 345, 390, 392, 396, 413, 414, 451, 471, 472, 473, 474, 475, 476, 477, 560, 579, 589, 598, 604, 695, 768, 774, 785, 787, 788, 789)\n" +
                "UNION ALL  \n" +
                "SELECT A.specid,A.ItemId,0 AS itemvalue, A.SubItemId,A.SubValue AS subitemvalue FROM ConfigSubItemSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE A.specid=#{specId}   and A.SubValue=1 and A.SubItemId in(19, 20, 196, 199, 202, 207, 210, 250, 329, 330, 331, 345, 390, 392, 396, 413, 414, 451, 471, 472, 473, 474, 475, 476, 477, 560, 579, 589, 598, 604, 695, 768, 774, 785, 787, 788, 789)\n" +
                "),\n" +
                "specconfigpic as ( \n" +
                " SELECT A.specid,A.itemid,A.subitemid,B.FilePath as picurl  FROM ConfigItem_RelationSpecPic AS A WITH(NOLOCK) \n" +
                " INNER JOIN car_spec_photo AS B WITH(NOLOCK)  ON A.picid = B.id WHERE A.specid=18  AND A.is_del=0 \n" +
                ")\n" +
                "SELECT count(1) FROM\n" +
                "specconfiginfo AS A WITH(NOLOCK) INNER JOIN ConfigItem AS B WITH(NOLOCK)  ON A.ItemId = B.Id\n" +
                "WHERE B.IsShow = 1";
        return sql;
    }

    public String getSpecParamListOfHighLightSql(Integer specId, Integer fuelType) {
        String sql = "select count(1) from (\n" +
                "select A.SpecId,A.ParamId,B.ParamName,A.ParamValue,0 as SubParamId,'' as SubParamName,ParamSort from ParamSpecRelation as A with(nolock) \n" +
                " inner join ParamItem as B with(nolock) on A.ParamId =B.ParamId  where specid = #{specId} and  A.ParamId  in (108,76,114,115,50,52,63)\n" +
                "union all \n" +
                "select A.SpecId ,A.ParamId,C.ParamName, '' as ParamValue,A.SubParamId,B.SubParamName ,ParamSort from ParamSpecSubItemValueRelation as A  with(nolock)\n" +
                "inner join ParamSubItem  as B with(nolock) on A.SubParamId =B.SubParamId \n" +
                "inner join ParamItem as C with(nolock) on A.ParamId =C.ParamId\n" +
                "where specid = #{specId} and A.ParamId in (72,73,74,86,87,43,55,62,122) \n" +
                ") as T ";

        if (fuelType == 4 || fuelType == 7) {//亮点配置-》 纯电车和氢能源车取值
            sql += "where T.ParamId in (50, 52, 55, 62, 63, 72, 73, 74, 76, 86, 87, 89, 90, 108, 114, 122)";
        } else if (fuelType == 3 || fuelType == 5 || fuelType == 6) {//亮点配置-》增程、插电取值,油电混
            sql += "where T.ParamId in (43, 50, 52, 55, 62, 63, 72, 73, 74, 76, 86, 87, 89, 90, 108, 114, 115, 122)";
        } else {//亮点配置-》  油车取值
            sql += "where T.ParamId in (43, 50, 52, 55, 86, 87, 89, 90, 108, 115)";
        }
        return sql;
    }

    public String get60picSpecIdListBySeriesSql(Integer seriesId) {
        String sql = "SELECT DISTINCT  A.SpecId FROM Car60PictureView as A WITH(NOLOCK) INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId = B.specId\n" +
                "WHERE A.is_del=0 and B.seriesId=#{seriesId} and A.picid>0 and  B.SpecState >= 20 and B.SpecState <= 30";
        return sql;
    }

    public String getSpecStandardConfigAndRelationPicItemListSql(Integer specId) {
        String sql = "WITH specconfiginfo AS\n" +
                "(\n" +
                "SELECT A.specid,A.ItemId,A.ItemValueId as itemvalue,0 AS subitemid,0 AS subitemvalue FROM ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE  A.specid=#{specId} and A.ItemValueId=1 \n" +
                "UNION ALL\n" +
                "SELECT A.specid,A.ItemId,0 as itemvalue,C.SubItemId,C.Value AS subitemvalue FROM  ConfigSpecRelation AS A WITH(NOLOCK)\n" +
                "INNER JOIN ConfigSubItemValueRelation as  C WITH(NOLOCK) ON A.ItemValueId  = C.ItemValueId \n" +
                "WHERE A.specid=#{specId}  and C.Value=1 \n" +
                "UNION ALL  \n" +
                "SELECT A.specid,A.ItemId,0 AS itemvalue, A.SubItemId,A.SubValue AS subitemvalue FROM ConfigSubItemSpecRelation AS A WITH(NOLOCK)\n" +
                "WHERE A.specid=#{specId}   and A.SubValue=1 \n" +
                "),\n" +
                "specconfigpic as ( \n" +
                " SELECT A.specid,A.itemid,A.subitemid,B.FilePath as picurl  FROM ConfigItem_RelationSpecPic AS A WITH(NOLOCK) \n" +
                " INNER JOIN car_spec_photo AS B WITH(NOLOCK)  ON A.picid = B.id WHERE A.specid=#{specId}  AND A.is_del=0 \n" +
                ")\n" +
                "SELECT A.SpecId,B.TypeId AS typeid,B.Sort AS itemorder,A.ItemId,cast(A.itemvalue as nvarchar) as itemvalue ,A.subitemid,A.subitemvalue, C.picurl FROM\n" +
                "specconfiginfo AS A WITH(NOLOCK) INNER JOIN ConfigItem AS B WITH(NOLOCK)  ON A.ItemId = B.Id\n" +
                "inner join configtype as D with(nolock) on B.typeId = D.id\n" +
                "left join specconfigpic as C WITH(NOLOCK) on A.ItemId=C.itemId and A.subitemid=C.subitemid\n" +
                "WHERE B.IsShow = 1 Order by D.sort,B.Sort";
        return sql;
    }

    public String getSpecParamItemListOfHighLightSql(Integer specId) {
        String sql = "select * from (\n" +
                "select A.SpecId,A.ParamId,B.ParamName,A.ParamValue,0 as SubParamId,'' as SubParamName,ParamSort from ParamSpecRelation as A with(nolock) \n" +
                " inner join ParamItem as B with(nolock) on A.ParamId =B.ParamId  where specid = #{specId} and  A.ParamId  in (108,76,114,115,50,52,63)\n" +
                "union all \n" +
                "select A.SpecId ,A.ParamId,C.ParamName, '' as ParamValue,A.SubParamId,B.SubParamName ,ParamSort from ParamSpecSubItemValueRelation as A  with(nolock)\n" +
                "inner join ParamSubItem  as B with(nolock) on A.SubParamId =B.SubParamId \n" +
                "inner join ParamItem as C with(nolock) on A.ParamId =C.ParamId\n" +
                "where specid = #{specId} and A.ParamId in (72,73,74,86,87,43,55,62,122) \n" +
                ") as T order by ParamSort";
        return sql;
    }

    public String getSpecParamItemListOfHighLight_cvSql(Integer specId) {
        String sql = "select * from (\n" +
                "select A.SpecId,A.ParamId,B.ParamName,A.ParamValue,0 as SubParamId,'' as SubParamName,ParamSort from ParamSpecRelation_CV as A with(nolock) \n" +
                " inner join ParamItem as B with(nolock) on A.ParamId =B.ParamId  where specid = #{specId} and  A.ParamId  in (108,76,114,115)\n" +
                "union all \n" +
                "select A.SpecId ,A.ParamId,C.ParamName, '' as ParamValue,A.SubParamId,B.SubParamName ,ParamSort from ParamSpecSubItemValueRelation_CV as A  with(nolock)\n" +
                "inner join ParamSubItem  as B with(nolock) on A.SubParamId =B.SubParamId \n" +
                "inner join ParamItem as C with(nolock) on A.ParamId =C.ParamId\n" +
                "where specid = #{specId} and A.ParamId in (72,73,74,86,87) \n" +
                ") as T order by ParamSort";
        return sql;
    }
}
