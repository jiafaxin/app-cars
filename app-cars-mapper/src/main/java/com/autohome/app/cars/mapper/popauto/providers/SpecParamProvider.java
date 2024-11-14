package com.autohome.app.cars.mapper.popauto.providers;

import com.autohome.app.cars.common.carconfig.Level;

/**
 * @author : zzli
 * @description : 车型参配
 * @date : 2024/2/26 14:36
 */
public class SpecParamProvider {
    /**
     * 75	NEDC纯电续航里程(km)
     * 101	CLTC纯电续航里程(km)
     * 135	WLTC纯电续航里程(km)
     * 79  电池快充时间(小时)
     * 81	电池慢充时间(小时)
     * 76	电池能量(kWh)
     */
    public String getSpecPartParamBySeriesId(int seriesid, int level) {

        if (Level.isCVLevel(level)) {
            return "  SELECT A.specid,S.SpecState,A.paramid,C.ParamName,A.ParamValue\n" +
                    "        FROM ParamSpecRelation_CV  A with(nolock)        \n" +
                    "\t\tJOIN CV_SpecView S WITH(NOLOCK) ON A.SpecId=s.specId\n" +
                    "        JOIN paramitem C with(nolock) ON C.paramid= A.ParamId\n" +
                    "        WHERE  C.paramid in (135,101,75,79,81,76) and\n" +
                    "\t\t S.SeriesId=#{seriesid} and S.specIsshow=1";
        } else {
            return " SELECT A.specid,S.SpecState,A.paramid,C.ParamName,A.ParamValue\n" +
                    "        FROM ParamSpecRelation A with(nolock)           \n" +
                    "\t\tJOIN SpecView S WITH(NOLOCK) ON A.SpecId=s.specId\n" +
                    "        JOIN paramitem C with(nolock) ON C.paramid= A.ParamId\n" +
                    "        WHERE C.paramid in (135,101,75,79,81,76) and\n" +
                    "        S.seriesId = #{seriesid}  and S.specIsshow = 1 AND S.SpecIsImage = 0";
        }
    }

    public String getSpecParamAll(int paramId, int specId) {
        String sql = "select B.parent as seriesId, A.ParamId, A.ParamValue, SpecId, SpecState\n" +
                "from ParamSpecRelation as A with (nolock)\n" +
                "         inner join spec_new as B with (nolock) on A.specid = B.id\n" +
                "where A.ParamId = #{paramId} %s\n" +
                "union\n" +
                "select B.seriesId as seriesId, A.ParamId, A.ParamValue, SpecId, SpecState\n" +
                "from ParamSpecRelation_cv as A with (nolock)\n" +
                "         inner join CV_Spec as B with (nolock) on A.specid = B.id\n" +
                "where A.ParamId = #{paramId} %s";
        String whereSql = "";
        if (specId > 0) {
            whereSql += " and A.specid = #{specId}";
        }

        return String.format(sql, whereSql, whereSql);
    }

    public String getSpecSubParamAll(int paramId, int specId) {
        String sql = "SELECT C.parent AS seriesId, A.SubParamId, B.SubParamName, SpecId, C.SpecState\n" +
                "FROM ParamSpecSubItemValueRelation AS A WITH (NOLOCK)\n" +
                "         INNER JOIN ParamSubItem AS B WITH (NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                "         JOIN spec_new AS C ON A.SpecId = C.id\n" +
                "WHERE A.paramid = #{paramId} %s\n" +
                "union\n" +
                "SELECT C.SeriesId AS seriesId, A.SubParamId, B.SubParamName, SpecId, C.SpecState\n" +
                "FROM ParamSpecSubItemValueRelation_CV AS A WITH (NOLOCK)\n" +
                "         INNER JOIN ParamSubItem AS B WITH (NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                "         JOIN CV_Spec AS C ON A.SpecId = C.id\n" +
                "WHERE A.paramid = #{paramId} %s";
        String whereSql = "";
        if (specId > 0) {
            whereSql += " and A.specid = #{specId}";
        }
        return String.format(sql, whereSql, whereSql);
    }

    public String getSpecParamBySeriesId(int seriesId, int level, int paramId) {
        if (Level.isCVLevel(level)) {
            return "select B.parent as seriesId, A.ParamId, A.ParamValue, SpecId, B.SpecState\n" +
                    "from ParamSpecRelation as A with (nolock)\n" +
                    "         inner join spec_new as B with (nolock) on A.specid = B.id\n" +
                    "where B.parent = #{seriesId} \n" +
                    "  and A.ParamId = #{paramId}";
        } else {
            return "select B.seriesId as seriesId, A.ParamId, A.ParamValue, SpecId, B.SpecState\n" +
                    "from ParamSpecRelation_cv as A with (nolock)\n" +
                    "         inner join CV_Spec as B with (nolock) on A.specid = B.id\n" +
                    "where B.seriesId = #{seriesId} \n" +
                    "  and A.ParamId = #{paramId}";
        }
    }

    public String getSpecSubParamBySeriesId(int seriesId, int level, int paramId) {
        if (Level.isCVLevel(level)) {
            return "SELECT C.parent AS seriesId, A.SubParamId, B.SubParamName, SpecId, C.SpecState\n" +
                    "FROM ParamSpecSubItemValueRelation AS A WITH (NOLOCK)\n" +
                    "         INNER JOIN ParamSubItem AS B WITH (NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                    "         JOIN spec_new AS C ON A.SpecId = C.id and C.parent = #{seriesId}\n" +
                    "WHERE A.paramid = #{paramId}";
        } else {
            return "SELECT C.SeriesId AS seriesId, A.SubParamId, B.SubParamName, SpecId, C.SpecState\n" +
                    "FROM ParamSpecSubItemValueRelation_CV AS A WITH (NOLOCK)\n" +
                    "         INNER JOIN ParamSubItem AS B WITH (NOLOCK) ON A.SubParamId = B.SubParamId\n" +
                    "         JOIN CV_Spec AS C ON A.SpecId = C.id and C.SeriesId = #{seriesId}\n" +
                    "WHERE A.paramid = #{paramId}";
        }
    }

    public String getSpecHighLightParamSql(String specIds) {
        return "select A.SpecId,A.ParamId,B.ParamName,A.ParamValue,0 as SubParamId,'' as SubParamName,ParamSort,B.logo from ParamSpecRelation as A with(nolock) \n" +
                " inner join ParamItem as B with(nolock) on A.ParamId =B.ParamId  where specid in (" + specIds + ") and  A.ParamId  in (108,76,114,115,50,52,63)\n" +
                "union all \n" +
                "select A.SpecId ,A.ParamId,C.ParamName, '' as ParamValue,A.SubParamId,B.SubParamName ,ParamSort,B.Logo from ParamSpecSubItemValueRelation as A  with(nolock)\n" +
                "inner join ParamSubItem  as B with(nolock) on A.SubParamId =B.SubParamId \n" +
                "inner join ParamItem as C with(nolock) on A.ParamId =C.ParamId\n" +
                "where specid in (" + specIds + ") and A.ParamId in (72,73,74,86,87,43,55,62,122) ";
    }

    /**
     * 新能源：1最高车速(km/h)>50最大功率(kW)>17轴距(mm)>座位数>长*宽*高
     * 油车：1最高车速(km/h)>102 WLTC综合油耗(L/100km)>49最大马力(Ps)>17轴距(mm)>长*宽*高>
     */
    public String getSpecPartParam1BySeriesId(int seriesid, int level) {
        if (Level.isCVLevel(level)) {
            return "  SELECT A.specid,S.SpecState,S.Seats as seats,A.paramid,C.ParamName,A.ParamValue,A.Modified_Stime\n" +
                    "        FROM ParamSpecRelation_CV  A with(nolock)        \n" +
                    "\t\tJOIN CV_SpecView S WITH(NOLOCK) ON A.SpecId=s.specId\n" +
                    "        JOIN paramitem C with(nolock) ON C.paramid= A.ParamId\n" +
                    "        WHERE  C.paramid in (1,102,49,50,17,110) and\n" +
                    "\t\t S.SeriesId=#{seriesid} and S.specIsshow=1 order by A.Modified_Stime desc";
        } else {
            return " SELECT A.specid,S.SpecState,S.specStructureSeat as seats,A.paramid,C.ParamName,A.ParamValue,A.Modified_Stime\n" +
                    "        FROM ParamSpecRelation A with(nolock)           \n" +
                    "\t\tJOIN SpecView S WITH(NOLOCK) ON A.SpecId=s.specId\n" +
                    "        JOIN paramitem C with(nolock) ON C.paramid= A.ParamId\n" +
                    "        WHERE C.paramid in (1,102,49,50,17,110) and\n" +
                    "        S.seriesId = #{seriesid}  and S.specIsshow = 1 AND S.SpecIsImage = 0 order by A.Modified_Stime desc";
        }
    }
}
