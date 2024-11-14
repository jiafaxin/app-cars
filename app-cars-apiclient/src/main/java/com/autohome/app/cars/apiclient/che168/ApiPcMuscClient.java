package com.autohome.app.cars.apiclient.che168;

import com.autohome.app.cars.apiclient.che168.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ApiPcMuscClient {

    /**
     * 获取车系 各城市保值率与全国平均保值率信息
     * 接口维护人：二手车-张晓磊
     * 接口文档：https://zhishi.autohome.com.cn/home/teamplace/file?targetId=h8Ya08C2Fs
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://apipcmusc.che168.com/v1/keepvalue/keepvaluebyseriesid?_appid=app&seriesId=${seriesId}",
            beta = "http://apipcmusc.che168.com/v1/keepvalue/keepvaluebyseriesid?_appid=app&seriesId=${seriesId}",
            online = "http://apipcmusc.che168.com/v1/keepvalue/keepvaluebyseriesid?_appid=app&seriesId=${seriesId}",
            userAgentHeader = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36",
            timeout = 1000
    )
    CompletableFuture<BaseModel<KeepValueSeriesResult>> getKeepValueBySeriesId(int seriesId);

}
