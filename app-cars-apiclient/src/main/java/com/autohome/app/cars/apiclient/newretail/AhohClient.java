package com.autohome.app.cars.apiclient.newretail;

import com.autohome.app.cars.apiclient.newretail.dtos.StoreSeriesItem;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface AhohClient {

    @AutoGet(
            dev = "http://tahoh.autohome.com.cn/api/community/sysStore/getStoreSeriesList?_appid=car&cityId=${cityId}",
            beta = "http://pahoh.autohome.com.cn/api/community/sysStore/getStoreSeriesList?_appid=car&cityId=${cityId}",
            online = "http://ahoh.inner.autohome.com.cn/api/community/sysStore/getStoreSeriesList?_appid=car&cityId=${cityId}"
    )
    CompletableFuture<BaseModel<List<StoreSeriesItem>>> getStoreSeriesList(int cityId);


}
