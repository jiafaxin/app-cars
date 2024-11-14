package com.autohome.app.cars.apiclient.openApi;

import com.autohome.app.cars.apiclient.openApi.dtos.SeriesAttentionResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface LnglatApiClient {

    /**
     * 获取车系下车系的关注度
     * 原接口维护人： 王晓龙
     * 原接口数据每天凌晨5点更新一次
     * @param seriesid 车系id
     * @return 车型的关注度
     */
    @AutoGet(
            dev = "http://lnglat.openapi.corpautohome.com/attention/spec/seriesid?seriesid=${seriesid}&APPKEY=7A3417F863CE917C5EA870A5507431EF",
            beta = "http://lnglat.openapi.corpautohome.com/attention/spec/seriesid?seriesid=${seriesid}&APPKEY=7A3417F863CE917C5EA870A5507431EF",
            online = "http://lnglat.openapi.corpautohome.com/attention/spec/seriesid?seriesid=${seriesid}&APPKEY=7A3417F863CE917C5EA870A5507431EF"
    )
    CompletableFuture<SeriesAttentionResult> getSeriesAtention(int seriesid);

}
