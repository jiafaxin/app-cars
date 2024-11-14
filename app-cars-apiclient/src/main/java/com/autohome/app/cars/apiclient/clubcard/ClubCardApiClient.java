package com.autohome.app.cars.apiclient.clubcard;

import java.util.concurrent.CompletableFuture;

import com.autohome.app.cars.apiclient.clubcard.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

@AutoHttpClient
public interface ClubCardApiClient {

    @AutoGet(
            dev = "http://la.corpautohome.com/club/club_koubei_get_tag_config?_appid=app&bbs_id=${seriesId}",
            beta = "http://la.corpautohome.com/club/club_koubei_get_tag_config?_appid=app&bbs_id=${seriesId}",
            online = "http://la.corpautohome.com/club/club_koubei_get_tag_config?_appid=app&bbs_id=${seriesId}"
    )
    CompletableFuture<BaseModel<SeriesClubCardTagResult>> getClubKoubeiTagConfig(int seriesId);

    @AutoGet(
            dev = "http://la.corpautohome.com/club/club_koubei_get_merge_data?_appid=app&bbs_id=${seriesId}&tag_id=${tagId}&sort=0&refine=0&is_club=1",
            beta = "http://la.corpautohome.com/club/club_koubei_get_merge_data?_appid=app&bbs_id=${seriesId}&tag_id=${tagId}&sort=0&refine=0&is_club=1",
            online = "http://la.corpautohome.com/club/club_koubei_get_merge_data?_appid=app&bbs_id=${seriesId}&tag_id=${tagId}&sort=0&refine=0&is_club=1"
    )
    CompletableFuture<BaseModel<SeriesClubCardDataResult>> getClubKoubeiData(int seriesId, int tagId);


    @AutoGet(
            dev = "http://maindata.api.autohome.com.cn/data/more/club_get_topics_list?_appid=app&club_bbs_id=${seriesId}&club_order_type=1&club_refine=0",
            beta = "http://maindata.api.autohome.com.cn/data/more/club_get_topics_list?_appid=app&club_bbs_id=${seriesId}&club_order_type=1&club_refine=0",
            online = "http://maindata.api.autohome.com.cn/data/more/club_get_topics_list?_appid=app&club_bbs_id=${seriesId}&club_order_type=1&club_refine=0"
    )
    CompletableFuture<BaseModel<SeriesClubTopicListResult>> getClubTopicsList(int seriesId);


    /**
     * http://la.corpautohome.com/doc/detail?laFlowId=1733
     * 查询车系资讯列表
     *
     * @param seriesId       车系ID
     * @param expressMessage 是否过滤快讯 1: 不返回快讯 0: 返回快讯
     * @param pageSize       每页个数
     * @param pageNum        页码
     * @return SeriesNewsResult
     */
    @AutoGet(
            dev = "http://la.corpautohome.com/basecar/series_news_list_car?_appid=car&series_id=${seriesId}&express_message=${expressMessage}&page_size=${pageSize}&page_num=${pageNum}",
            beta = "http://la.corpautohome.com/basecar/series_news_list_car?_appid=car&series_id=${seriesId}&express_message=${expressMessage}&page_size=${pageSize}&page_num=${pageNum}",
            online = "http://la.corpautohome.com/basecar/series_news_list_car?_appid=car&series_id=${seriesId}&express_message=${expressMessage}&page_size=${pageSize}&page_num=${pageNum}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<SeriesNewsResult>> getSeriesNews(int seriesId, int expressMessage, int pageSize, int pageNum);
}
