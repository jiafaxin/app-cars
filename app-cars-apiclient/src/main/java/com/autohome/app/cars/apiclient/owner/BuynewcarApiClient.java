package com.autohome.app.cars.apiclient.owner;

import com.autohome.app.cars.apiclient.owner.dtos.ZhaoyouhuiResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface BuynewcarApiClient {

    @AutoGet(
            dev = "http://buynewcar.api.in.autohome.com.cn/api/series/bidding/v2/portal?_appid=app&cityid=${cityId}&seriesid=${seriesId}",
            beta = "http://buynewcar.api.in.autohome.com.cn/api/series/bidding/v2/portal?_appid=app&cityid=${cityId}&seriesid=${seriesId}",
            online = "http://buynewcar.api.in.autohome.com.cn/api/series/bidding/v2/portal?_appid=app&cityid=${cityId}&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<ZhaoyouhuiResult>> getZhaoyouhui(int seriesId, int cityId);


}
