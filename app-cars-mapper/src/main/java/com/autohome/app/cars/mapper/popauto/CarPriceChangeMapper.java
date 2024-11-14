package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.PriceChangeEntity;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : zzli
 * @description : 车型降价
 * @date : 2024/10/24 11:20
 */
@Mapper
@DS("popauto")
public interface CarPriceChangeMapper {

    /**
     * 获取所有直降、限时降数据
     */
    @Select("select brandid,seriesid,specId,startTime,endtime,changetype,priceGap,targetPrice,description,articleUrl,articleId,createdstime from (\n" +
            "\tselect * ,ROW_NUMBER ()over(partition by specid order by startTime DESC) as rn from (\n" +
            "\t\tselect B.brandid,B.seriesid, A.specId,B.specname,A.startTime,A.endTime,A.created_stime as createdstime,\n" +
            "\t\tA.changetype,A.priceGap,A.targetPrice,A.description,A.articleUrl,A.articleId \n" +
            "\t\tfrom Fct_Price_Change as A with(nolock) inner join specview as B with(nolock) on A.specid=B.specid \n" +
            "\t\twhere B.specstate>=20 and B.specstate<=30 and A.changeType in (40,50) and A.is_del=0 \n" +
            "\t\tand A.priceGap>0 and (GETDATE() BETWEEN A.startTime and A.endtime)\n" +
            ") as T) as PT  where rn = 1 order by createdstime DESC ")
    List<PriceChangeEntity> GetAllList();
}
