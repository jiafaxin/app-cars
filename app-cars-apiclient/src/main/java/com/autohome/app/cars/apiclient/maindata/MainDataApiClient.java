package com.autohome.app.cars.apiclient.maindata;

import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface MainDataApiClient {

    /**
     * http://wiki.corpautohome.com/pages/viewpage.action?pageId=136741267
     * 获取热点数据
     * 业务类型：论坛-club、资讯文章-cms、资讯视频-video、口碑-koubei等
     * 热点类型：浏览数-pv、播放数-vv、评论数-reply_count、点赞数-like_count等
     *
     * @param bizIdTypes(业务类型-热点类型-业务id)，如： club-pv-12,club-vv-22,chejiahao-pv-1,test-pv-10,test-vv-11
     *                                      tp99: 10ms
     * @param bizIdTypes
     * @return
     */
    @AutoGet(
            dev = "http://maindata.api.autohome.com.cn/hotdata/get_hot_data?_appid=app&value=${bizIdTypes}",
            beta = "http://maindata.api.autohome.com.cn/hotdata/get_hot_data?_appid=app&value=${bizIdTypes}",
            online = "http://maindata.api.autohome.com.cn/hotdata/get_hot_data?_appid=app&value=${bizIdTypes}",
            timeout = 50
    )
    CompletableFuture<BaseModel<List<HotDataResult>>> getHotData(String bizIdTypes);



    @AutoGet(
            dev = "http://la.corpautohome.com/cms/series_news_list?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            beta = "http://la.corpautohome.com/cms/series_news_list?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            online = "http://la.corpautohome.com/cms/series_news_list?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            timeout = 5000
    )
    CompletableFuture<BaseModel<MainDataSeriesSummaryFeedsResult>> getNewMainDataSeriesSummaryFeeds(int series_id, String info_type, String search_after, int page_size);

    @AutoGet(
            dev = "http://maindata.api.autohome.com.cn/data/page/maindata_get_multiple_infos?_appid=app&fields=${fields}&mainDataIds=${mainDataIds}",
            beta = "http://maindata.api.autohome.com.cn/data/page/maindata_get_multiple_infos?_appid=app&fields=${fields}&mainDataIds=${mainDataIds}",
            online = "http://maindata.api.autohome.com.cn/data/page/maindata_get_multiple_infos?_appid=app&fields=${fields}&mainDataIds=${mainDataIds}",
            timeout = 10000
    )
    CompletableFuture<BaseModel<List<MultipleInfoFeed>>> getMultipleInfos(String fields, String mainDataIds);

    @AutoGet(
            dev = "http://la.corpautohome.com/video/series_news_list_v2?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            beta = "http://la.corpautohome.com/video/series_news_list_v2?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            online = "http://la.corpautohome.com/video/series_news_list_v2?_appid=app&series_id=${series_id}&channel=all&info_type=${info_type}&search_after=${search_after}&page_size=${page_size}&express_message=1",
            timeout = 5000
    )
    CompletableFuture<BaseModel<MainDataSeriesSummaryFeedsResult>> getNewMainDataSeriesSummaryFeedsV2(int series_id, String info_type, String search_after, int page_size);

    @AutoGet(
            dev = "http://news.app.autohome.com.cn/newsext/series_news/get_top_news?seriesId=${series_id}",
            beta = "http://news.app.autohome.com.cn/newsext/series_news/get_top_news?seriesId=${series_id}",
            online = "http://news.app.autohome.com.cn/newsext/series_news/get_top_news?seriesId=${series_id}",
            timeout = 5000
    )
    CompletableFuture<BaseModel<List<SeriesAllTabResult>>> getAllTabMainDataSeriesSummaryFeeds(int series_id);

}
