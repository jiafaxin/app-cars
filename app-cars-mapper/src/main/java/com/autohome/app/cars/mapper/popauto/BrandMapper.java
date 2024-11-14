package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.mapper.popauto.entities.BrandFctSeriesEntity;
import com.autohome.app.cars.mapper.popauto.providers.BrandProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface BrandMapper {

    @SelectProvider(value = BrandProvider.class,method = "getBrand")
    BrandEntity getBrand(int brandId);

    @SelectProvider(value = BrandProvider.class,method = "getAllBrands")
    List<BrandEntity> getAllBrands();

    @SelectProvider(value = BrandProvider.class,method = "getBrandList")
    List<BrandEntity> getBrandList(List<Integer> seriesIds);

    @Select("SELECT distinct s.fctid,\n" +
            "                s.fctPy,\n" +
            "\t\t\t\ts.seriesId,\n" +
            "\t\t\t\ts.SeriesState,\n" +
            "                c.scores_ranks\n" +
            "FROM SeriesView s WITH (NOLOCK)\n" +
            "    LEFT JOIN [Replication].dbo.dxp_CarBrandSeries_Ranks c WITH (NOLOCK) ON s.seriesid= c.series_id and isdelete=0\n" +
            "WHERE s.brandid = #{brandId} and s.seriesPhotoNum >= 3\n" +
            "ORDER BY c.scores_ranks")
    List<BrandFctSeriesEntity> getBrandSeriesIds(int brandId);


    @Select("WITH brandlist AS\n" +
            "                 (\n" +
            "                     SELECT brandid, FirstLetter, BrandName, sellState\n" +
            "                     FROM (\n" +
            "                              SELECT brandid,\n" +
            "                                     FirstLetter,\n" +
            "                                     BrandName,\n" +
            "                                     sellState,\n" +
            "                                     ROW_NUMBER() over(partition BY brandid ORDER BY sellState DESC) AS RN\n" +
            "                              FROM (\n" +
            "                                       SELECT brandid,\n" +
            "                                              FirstLetter,\n" +
            "                                              BrandName,\n" +
            "                                              CASE IsPublic WHEN 1 THEN 1 ELSE 0 END AS sellState\n" +
            "                                       FROM [CarManuePic]\n" +
            "                                       WITH (NOLOCK)\n" +
            "                                   ) AS T) AS TT\n" +
            "                     WHERE RN = 1\n" +
            "                 ),\n" +
            "             newEngergyBrand AS(\n" +
            "                 select distinct newFctid as brandid,IsNewenergy as havenewenergy  from brands WITH (NOLOCK) where IsNewenergy =1\n" +
            "            )\n" +
            "        SELECT distinct cmp.brandid      id,\n" +
            "                        cmp.firstLetter,\n" +
            "                        cmp.brandname    name,\n" +
            "                        cmp.sellState as state,\n" +
            "                        g.img            ,\n" +
            "                        isnull(negb.havenewenergy,0) as isNewEnergy,\n" +
            "                        isnull( s.sameletterrankbyorders,99999) as  ordercls\n" +
            "        FROM BrandList cmp WITH (NOLOCK)\n" +
            "        \tleft join newEngergyBrand as negb on cmp.brandid=negb.brandid\n" +
            "            left join [group] g WITH (NOLOCK) on cmp.brandid=g.id\n" +
            "            LEFT JOIN dxp_CarBrandSeries_Ranks s WITH (NOLOCK)\n" +
            "        ON g.id= s.brandid AND s.typeid=1 AND s.is_del =0\n" +
            "        ORDER BY FirstLetter")
    List<BrandEntity> getAllBrandSort();


}
