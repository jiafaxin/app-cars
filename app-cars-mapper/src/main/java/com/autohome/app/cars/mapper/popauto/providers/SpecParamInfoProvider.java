package com.autohome.app.cars.mapper.popauto.providers;

import com.autohome.app.cars.common.carconfig.Level;
import org.apache.ibatis.annotations.Param;

/**
 * @author : lihongchen
 * @description : 车型参数信息
 * @date : 2024/4/19 10:45
 */
public class SpecParamInfoProvider {
    public static String getParamsBySpecIds(String specIdsStr, boolean isCV) {
        String tableName1 = isCV ? "ParamSpecRelation_CV" : "ParamSpecRelation";
        String tableName2 = isCV ? "ParamSpecSubItemValueRelation_CV" : "ParamSpecSubItemValueRelation";

        String sql = "SELECT\n" +
                "        psr.specid,\n" +
                "        psr.paramid,\n" +
                "        pi.ParamName,\n" +
                "        pi.DisplayType,\n" +
                "        pi.datatype,\n" +
                "        pi.DynamicShow,\n" +
                "        psr.ParamValue,\n" +
                "        NULL AS SubParamSort,\n" +
                "        NULL AS SubParamName,\n" +
                "        NULL AS SubParamId,\n" +
                "        NULL AS SubParamTextValue,\n" +
                "        0 AS subParamValue,\n" +
                "        NULL AS price\n" +
                "        FROM \n" +
                "\t\t\t\t${tableName1} psr with(nolock)\n" +
                "        JOIN paramitem pi with(nolock) ON pi.paramid= psr.ParamId\n" +
                "        WHERE\n" +
                "        psr.SpecId in (${specIdsStr}) UNION ALL\n" +
                "        SELECT\n" +
                "        pssvr.specid,\n" +
                "        pssvr.paramid,\n" +
                "        pi.ParamName,\n" +
                "        pi.DisplayType,\n" +
                "        pi.datatype,\n" +
                "        pi.DynamicShow,\n" +
                "        NULL AS ParamValue,\n" +
                "        psi.SubParamSort,\n" +
                "        psi.SubParamName,\n" +
                "        pssvr.SubParamId,\n" +
                "        pssvr.SubParamTextValue,\n" +
                "        pssvr.subParamValue,\n" +
                "        pssvr.price\n" +
                "        FROM\n" +
                "\t\t\t\t${tableName2} pssvr with(nolock)\n" +
                "        JOIN ParamSubItem psi with(nolock) ON psi.SubParamId= pssvr.SubParamId\n" +
                "        JOIN paramitem pi with(nolock) ON pi.paramid= psi.paramid\n" +
                "        WHERE\n" +
                "        pssvr.SpecId in (${specIdsStr})";

        // 根据条件选择不同的表
        sql = sql.replace("${tableName1}", tableName1);
        sql = sql.replace("${tableName2}", tableName2);
        sql = sql.replace("${specIdsStr}", specIdsStr);

        return sql;
    }

    public String getAllParamType() {
        String sql = "select ParamTypeId, ParamTypeName, TypeSort\n" +
                "        from ParamType pt with(nolock)\n" +
                "        order by pt.TypeSort";
        return sql;
    }


    public String getAllParamItems() {
        String sql = "select A.*\n" +
                "        from paramitem as A with(Nolock)  inner join ParamType as B\n" +
                "        with (nolock)\n" +
                "        on A.TypeId = B.ParamTypeId\n" +
                "        order by B.TypeSort, ParamSort";
        return sql;
    }

}
