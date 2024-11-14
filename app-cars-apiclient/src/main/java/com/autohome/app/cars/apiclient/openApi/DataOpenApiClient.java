package com.autohome.app.cars.apiclient.openApi;

import com.autohome.app.cars.apiclient.openApi.dtos.SameLevelRecommendSeriesResult;
import com.autohome.app.cars.apiclient.openApi.dtos.SeriesHotEventResult;
import com.autohome.app.cars.apiclient.openApi.dtos.SeriesRecommendLikeResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * 推荐数据
 */
@AutoHttpClient
public interface DataOpenApiClient {

    /**
     * 获取车系同级车是否有数据
     * 原接口数据每天凌晨0点更新一次
     *
     * @return 车型的关注度
     */
    @AutoGet(
            dev = "http://data.in.corpautohome.com/oneapi/v2?pid=${pid}&operation=0&version=1&number=${num}&ext=${ext}",
            beta = "http://data.in.corpautohome.com/oneapi/v2?pid=${pid}&operation=0&version=1&number=${num}&ext=${ext}",
            online = "http://data.in.corpautohome.com/oneapi/v2?pid=${pid}&operation=0&version=1&number=${num}&ext=${ext}",
            authorization = "Basic YXBwLWNhcmluZm8tYXBpOnMwVVBrRTRVRjc="

    )
    CompletableFuture<SeriesRecommendLikeResult> getRecommendLikeSeriesList(int pid,int num,String ext);



    /**
     * 获取车系同级车是否有数据
     * 原接口数据每天凌晨0点更新一次
     *
     * @return 车型的关注度
     */
    @AutoGet(
            dev = "http://data.in.corpautohome.com/oneapi/v2?pid=90100239&uuid=2086B9FA437344A45F3A2AB106A52F0B&devicetype=android&appversion=11.60.0&uid=&source=app&operation=0&netstate=5&version=3&userip=&dataformat=2",
            beta = "http://data.in.corpautohome.com/oneapi/v2?pid=90100239&uuid=2086B9FA437344A45F3A2AB106A52F0B&devicetype=android&appversion=11.60.0&uid=&source=app&operation=0&netstate=5&version=3&userip=&dataformat=2",
            online = "http://data.in.corpautohome.com/oneapi/v2?pid=90100239&uuid=2086B9FA437344A45F3A2AB106A52F0B&devicetype=android&appversion=11.60.0&uid=&source=app&operation=0&netstate=5&version=3&userip=&dataformat=2",
            authorization = "Basic Y2FyZXh0OmhVWDB0aW5FJGY="
    )
    CompletableFuture<SeriesHotEventResult> getHotSeriesNews();

    /**
     * 车系tab同级车推荐
     *
     * @param city_id
     * @param pid
     * @param device_id
     * @param device_type
     * @param version
     * @param series_id
     * @return
     */
    @AutoGet(
            dev = "http://test.search.guess.corpautohome.com/guess/sameLeve?city_id=${city_id}&pid=${pid}&device_id=${device_id}&device_type=${device_type}&version=${version}&series_id=${series_id}",
            beta = "http://search.guess.corpautohome.com/guess/sameLeve?city_id=${city_id}&pid=${pid}&device_id=${device_id}&device_type=${device_type}&version=${version}&series_id=${series_id}",
            online = "http://search.guess.corpautohome.com/guess/sameLeve?city_id=${city_id}&pid=${pid}&device_id=${device_id}&device_type=${device_type}&version=${version}&series_id=${series_id}"
    )
    CompletableFuture<SameLevelRecommendSeriesResult> getGuessSameLevel(int city_id, int pid, String device_id, String device_type, String version, int series_id);
}
