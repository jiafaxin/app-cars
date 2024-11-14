package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.SeriesSubscribeNewsEntity;
import com.autohome.app.cars.mapper.appcars.providers.SeriesSubscribeNewsProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/8/13 11:03
 */
@Mapper
@DS("appcars")
public interface SeriesSubscribeNewsMapper {

    @SelectProvider(type = SeriesSubscribeNewsProvider.class, method = "selectLastAllType_v57")
    List<SeriesSubscribeNewsEntity> selectLastAllType(Integer seriesId);

    /**
     * 批量保存
     * @param list
     */
    @SelectProvider(type = SeriesSubscribeNewsProvider.class, method = "saveAll")
    void saveAll(List<SeriesSubscribeNewsEntity> list);

    /**
     * 动态消息查询
     */
    @SelectProvider(type = SeriesSubscribeNewsProvider.class, method = "pageGetListSql")
    List<SeriesSubscribeNewsEntity>pageGetList(int cityId,List<Integer>seriesIds,List<Integer>specIds,int pageSize,String searchAfterRequest);

    /**
     * 根据给定的时间范围查询更新的消息量
     */
    @SelectProvider(type = SeriesSubscribeNewsProvider.class, method = "getUpdatedCountByTimeRangeSql")
    Integer getUpdatedCountByTimeRange(int cityId,List<Integer>seriesIds,List<Integer>specIds,String date);


    @SelectProvider(type = SeriesSubscribeNewsProvider.class, method = "getSubscribeSeriesListSql")
    List<SeriesSubscribeNewsEntity> getSubscribeSeriesList(List<Integer> seriesIdList, List<Integer> specIdList, int cityid);

    @Delete("DELETE FROM series_subscribe_news WHERE biz_type IN (7,8,9,10)")
    int cleanPriceDown();

    @Delete("DELETE FROM series_subscribe_news WHERE biz_type IN (1,4)")
    int cleanPicAndCarWork();
    @Delete("DELETE FROM series_subscribe_news WHERE biz_type = #{type}")
    int cleanDataByType(int type);
}
