package com.autohome.app.cars.apiclient.reply;

import com.autohome.app.cars.apiclient.reply.dtos.NewSeriesAiViewPointResult;
import com.autohome.app.cars.apiclient.reply.dtos.SereisReplyResult;
import com.autohome.app.cars.apiclient.reply.dtos.SeriesAiViewPointResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 回复相关接口
 */
@AutoHttpClient
public interface ReplyApiClient {

    @AutoGet(
            dev = "http://reply.cupid.autohome.com.cn/api/2/series/commentCount.ashx?seriesId=${seriesId}&isAll=1",
            beta = "http://reply.cupid.autohome.com.cn/api/2/series/commentCount.ashx?seriesId=${seriesId}&isAll=1",
            online = "http://reply.autohome.com.cn/api/2/series/commentCount.ashx?seriesId=${seriesId}&isAll=1"
    )
    CompletableFuture<BaseModel<Integer>> getSereisReplyCount(int seriesId);

    @AutoGet(
            dev = "http://reply.corpautohome.com/api/viewpoint/series?_appid=app&seriesid=${seriesid}&authorization=${authorization}&pm=${pm}&pluginversion=${pluginversion}&deviceid=${deviceid}",
            beta = "http://reply.corpautohome.com/api/viewpoint/series?_appid=app&seriesid=${seriesid}&authorization=${authorization}&pm=${pm}&pluginversion=${pluginversion}&deviceid=${deviceid}",
            online = "http://reply.corpautohome.com/api/viewpoint/series?_appid=app&seriesid=${seriesid}&authorization=${authorization}&pm=${pm}&pluginversion=${pluginversion}&deviceid=${deviceid}"
    )
    CompletableFuture<BaseModel<SeriesAiViewPointResult>> getSeriesAiViewPoint(int seriesid, int pm, String pluginversion, String deviceid, String authorization);

    @AutoGet(
            dev = "http://reply.corpautohome.com/api/viewpoint/seriespoints?_appid=app&seriesid=${seriesid}&deviceid=${deviceid}",
            beta = "http://reply.corpautohome.com/api/viewpoint/seriespoints?_appid=app&seriesid=${seriesid}&deviceid=${deviceid}",
            online = "http://reply.corpautohome.com/api/viewpoint/seriespoints?_appid=app&seriesid=${seriesid}&deviceid=${deviceid}"
    )
    CompletableFuture<BaseModel<List<NewSeriesAiViewPointResult>>> getNewSeriesAiViewPoint(int seriesid,String deviceid);
}
