package com.autohome.app.cars.apiclient.im;

import com.autohome.app.cars.apiclient.im.dtos.SeriesImResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ChatApiClient {
    @AutoGet(
            dev = "http://chat.api.in.autohome.com.cn/c905/s1/api/getSeriesCitys?_appid=app&seriesId=${seriesId}",
            beta = "http://chat.api.in.autohome.com.cn/c905/s1/api/getSeriesCitys?_appid=app&seriesId=${seriesId}",
            online = "http://chat.api.in.autohome.com.cn/c905/s1/api/getSeriesCitys?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<SeriesImResult>> getSeriesCitys(int seriesId);
}
