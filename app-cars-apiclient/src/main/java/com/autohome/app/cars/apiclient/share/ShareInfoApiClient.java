package com.autohome.app.cars.apiclient.share;

import com.autohome.app.cars.apiclient.share.dtos.ShareInfoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ShareInfoApiClient {

    /**
     * 车品搜索列表接口
     */
    @AutoGet(
            dev = "http://shorten.athm.cn/port?_appid=app&ttl=${ttl}&url=${url}",
            beta = "http://shorten.athm.cn/port?_appid=app&ttl=${ttl}&url=${url}",
            online = "http://shorten.athm.cn/port?_appid=app&ttl=${ttl}&url=${url}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<ShareInfoResult>> getShareInfo(String ttl, String url);

}
