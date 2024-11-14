package com.autohome.app.cars.apiclient.opscard;

import com.autohome.app.cars.apiclient.opscard.dtos.NewSeriesCityHotNewsAndTabResult;
import com.autohome.app.cars.apiclient.opscard.dtos.SeriesHotNewsResult;
import com.autohome.app.cars.apiclient.opscard.dtos.SeriesReplyGuideTextResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * 运营配置相关接口
 */
@AutoHttpClient
public interface OpsCardApiClient {

    @AutoGet(
            dev = "http://opscardtest.terra.corpautohome.com/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_reply_page&a=3&v=11.61.7&pm=1&model=&cid=-2",
            beta = "http://opscardtest.terra.corpautohome.com/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_reply_page&a=3&v=11.61.7&pm=1&model=&cid=-2",
            online = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_reply_page&a=3&v=11.61.7&pm=1&model=&cid=-2"
    )
    CompletableFuture<BaseModel<SeriesReplyGuideTextResult>> getSereisReplyGuideInfo();

    // TODO chengjincheng 2024/7/19 测试完成后，预发的model改为1，和线上一致
    @AutoGet(
            dev = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_hot_news&a=2&v=11.64.8&pm=1&model=0&cid=${cityId}",
            beta = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_hot_news&a=2&v=11.64.8&pm=1&model=0&cid=${cityId}",
            online = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=series_hot_news&a=2&v=11.64.8&pm=1&model=1&cid=${cityId}"
    )
    CompletableFuture<BaseModel<SeriesHotNewsResult>> getSeriesHotNews(int cityId);


    @AutoGet(
            dev = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=new_series_hot_news&a=2&v=11.66.0&pm=1&model=0&cid=${cityId}",
            beta = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=new_series_hot_news&a=2&v=11.66.0&pm=1&model=0&cid=${cityId}",
            online = "http://opscard.api.lq.autohome.com.cn/app-ops-product-api/pageCard/queryPageCardData?pagetag=new_series_hot_news&a=2&v=11.66.0&pm=1&model=1&cid=${cityId}"
    )
    CompletableFuture<BaseModel<NewSeriesCityHotNewsAndTabResult>> getSeriesHotNewsAndTab(int cityId);

}
