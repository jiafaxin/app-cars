package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.common.utils.KeyValueDto;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.mapper.popauto.providers.SeriesProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@DS("popauto")
public interface SeriesMapper {

    @Select("SELECT A.id FROM Brands AS A WITH(NOLOCK) where isdel = 0")
    List<Integer> getAllSeriesIds();

    /**
     * 获取所有新能源车系ID
     * @return 新能源车系ID List
     */
    @Select("SELECT A.id FROM Brands AS A WITH(NOLOCK) where A.IsNewenergy=1 and isdel = 0")
    List<Integer> getAllNewEnergySeriesIds();

    @Select("SELECT A.id as [key],A.name as [value] FROM Brands AS A WITH(NOLOCK) where isdel = 0")
    List<KeyValueDto<Integer,String>> getAllSeriesIdAndName();

    @SelectProvider(value = SeriesProvider.class, method = "getAllSeries")
    List<SeriesEntity> getAllSeries();
    /**
     * 所有新能源车系
     */
    @SelectProvider(value = SeriesProvider.class, method = "getgetAllEnergySeries")
    List<SeriesEntity> getgetAllEnergySeries();
    @SelectProvider(value = SeriesProvider.class, method = "getSeries")
    SeriesEntity getSeries(int seriesId);

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesList")
    List<SeriesEntity> getSeriesList(List<Integer> seriesIds);

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesHasBookSpec")
    SeriesHasBookSpecEntity getSeriesHasBookSpec(int seriesId);

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesHasBookSpecAll")
    List<SeriesHasBookSpecEntity> getSeriesHasBookSpecAll();

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesFuelTypeDetail")
    List<SeriesFuelTypeEntity> getSeriesFuelTypeDetail(int seriesId);

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesFuelTypeDetailAll")
    List<SeriesFuelTypeEntity> getSeriesFuelTypeDetailAll();

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesParamIsShow")
    List<Integer> getSeriesParamIsShow(int seriesId);

    @SelectProvider(value = SeriesProvider.class, method = "getSeriesParamIsShowAll")
    List<Integer> getSeriesParamIsShowAll();

    @SelectProvider(value = SeriesProvider.class, method = "getAllStopSeriesIsImageSpec")
    List<Integer> getAllStopSeriesIsImageSpec();

    @SelectProvider(value = SeriesProvider.class, method = "getStopSeriesIsImageSpec")
    List<Integer> getStopSeriesIsImageSpec(int seriesId);

    /**
     * 获取指定车系一条AI视频信息
     * http://carservice.autohome.com.cn/camera/aivideo/getAiVideoOrderBySeriesId?seriesId=6700&_appId=app
     */
    @SelectProvider(value = SeriesProvider.class, method = "getVideoOrderBySeriesId")
    List<VideoOrderModel> getVideoOrderBySeriesId(@Param("seriesId") Integer seriesId);
    /**
     * 车系隐藏配置差异列表
     */
    @Select("select id from Brands with(nolock) where diffconfigisshow =0")
    List<Integer> getHideSeriesDiffConfig();

    /**
     * 获取在售状态下乘用车热度排名信息
     * @return
     */
    @Select("select seriesId,seriesNewRank,seriesState from SeriesView WITH (NOLOCK) where levelId not in (11,12,13,14,25) and SeriesState in (20,30)")
    List<SeriesAttentionRankEntity> getOnSaleSeriesAttentionRankList();

    @Select("SELECT seriesId FROM SeriesView WITH (NOLOCK) where SeriesState in (20,30)")
    List<Integer> getAllOnSaleSeriesIds();
}
