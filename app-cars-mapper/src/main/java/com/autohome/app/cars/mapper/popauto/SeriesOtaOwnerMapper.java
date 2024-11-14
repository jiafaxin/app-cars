package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.ChargeStationModel;
import com.autohome.app.cars.mapper.popauto.entities.OtaUpgradeModel;
import com.autohome.app.cars.mapper.popauto.entities.OwnerRightsModel;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
@DS("popauto")
public interface SeriesOtaOwnerMapper {


    @Select("<script>" +
            "  select *\n" +
            "from (select row_number() over (order by T.pushTime desc) as RowId,\n" +
            "T.id,\n" +
            "T.version,\n" +
            "T.pushTime,\n" +
            "T.level,\n" +
            "T.levelTag,\n" +
            "T.summary,\n" +
            "T.content,\n" +
            "T.brandId,\n" +
            "T.seriesId,\n" +
            "T.created_stime,\n" +
            "T.modified_stime,\n" +
            "T.is_del,\n" +
            "T.userId,\n" +
            "T.remark,\n" +
            "B.name as seriesName,\n" +
            "B.nobgcolorpicurl as seriesImg\n"+
            " \n from OtaUpgrade as T\n" +
            " join Brands as B on T.seriesId = B.id \n" +
            "\nWHERE\n" +
            "\tT.is_del = 0" +
            " <if test=\"seriesId > 0\"> " +
            "   AND T.seriesId = #{seriesId} " +
            " </if> " +
            " <if test='itemTag != null and itemTag.size > 0'> " +
            "  and exists (select 1 from OtaUpgradeDetail as tt where tt.is_del = 0 and tt.upgradeId = T.id and itemName in \n" +
            "   <foreach collection=\"itemTag\" item=\"tag\" index=\"index\" open=\"(\" close=\")\" separator=\",\">\n" +
            "     #{tag}\n" +
            "   </foreach>" +
            "   )" +
            " </if> " +
            " ) as TT\n" +
            "where TT.RowId between #{start} and #{end}\n" +
            "order by pushTime desc" +
            "</script>")
    List<OtaUpgradeModel> getOtaUpgrades(@Param("seriesId") Integer seriesId,
                                         @Param("itemTag") List<String> itemTag,
                                         @Param("start") int start,
                                         @Param("end") int end);

    @Select("<script>" +
            " select \n" +
            "A.id,\n" +
            "       A.type,\n" +
            "       A.name,\n" +
            "       A.tags,\n" +
            "       A.content,\n" +
            "       A.rank,\n" +
            "       A.seriesId,\n" +
            "       A.brandId,\n" +
            "       A.created_stime,\n" +
            "       A.modified_stime,\n" +
            "       A.is_del,\n" +
            "       A.userId,\n" +
            "       A.remark,\n" +
            "       B.name as seriesName,\n" +
            "       B.nobgcolorpicurl as seriesImg\n"+
            " from OwnerRights as A\n" +
            "         join Brands as B on A.seriesId = B.id\n" +
            "\nWHERE\n" +
            "\tA.is_del = 0" +
            " <if test=\"seriesId > 0\"> " +
            "   AND A.seriesId = #{seriesId} " +
            " </if> " +
            " <if test=\"specId > 0\"> " +
            "   AND exists(select 1 from OwnerRightsSpec as tt where tt.is_del = 0 and tt.ownerId = A.id and specId =  #{specId}) " +
            " </if> " +
            " order by A.rank" +
            "</script>")
    List<OwnerRightsModel> getOwnerRights(@Param("seriesId") Integer seriesId, @Param("specId") Integer specId);


    @Select("<script>" +
            " select \n" +
            "T.id,\n" +
            "       T.name,\n" +
            "       T.brandId,\n" +
            "       A.seriesId,\n" +
            "       T.payTypeId AS payType,\n" +
            "       C.payTypeName,\n" +
            "       T.payInfo,\n" +
            "       T.price,\n" +
            "       T.payItemInfo,\n" +
            "       T.sort,\n" +
            "       T.is_del,\n" +
            "       T.created_stime,\n" +
            "       T.modified_stime,\n" +
            "       T.userId,\n" +
            "       B.name            as seriesName,\n" +
            "       B.nobgcolorpicurl as seriesImg\n"+
            "\nfrom ChargeStation as T\n" +
            "         join (select distinct seriesId, chargeId,is_del from ChargeStationSpec) as A on T.id = A.chargeId\n" +
            "         join Brands as B on A.seriesId = B.id\n" +
            "         join ChargeStationPayType as C on T.payTypeId = C.id\n" +
            "where T.is_del = 0 AND A.is_del = 0 AND A.seriesId = #{seriesId}" +
            " <if test=\"chargeId > 0\"> " +
            "   AND T.chargeId = #{chargeId} " +
            " </if> " +
            " order by T.sort" +
            "</script>")
    List<ChargeStationModel> getChargeStations(@Param("seriesId") Integer seriesId, @Param("chargeId") Integer chargeId);

    @Select("select (select count(1) from OtaUpgrade where is_del = 0 and seriesId = #{seriesId})                                as historyCount,\n" +
            "       (select count(1) from OtaUpgrade where is_del = 0 and seriesId = #{seriesId} and pushTime > getdate() - 183) as semiyearCount")
    HashMap<String,Integer> getOtaUpgradeStatistics(@Param("seriesId") Integer seriesId);
}
