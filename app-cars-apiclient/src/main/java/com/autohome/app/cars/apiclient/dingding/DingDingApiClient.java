package com.autohome.app.cars.apiclient.dingding;

import com.autohome.app.cars.apiclient.dingding.dtos.DingDingApiResult;
import com.autohome.app.cars.apiclient.dingding.dtos.DingDingMessageParam;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;
import com.autohome.app.cars.common.httpclient.annotation.PostBody;

import java.util.concurrent.CompletableFuture;

/**
 * Created by dx on 2024/9/5
 * 钉钉消息发送
 */
@AutoHttpClient
public interface DingDingApiClient {
    @AutoPost(
            dev = "http://dingding.oa.corpautohome.com/send?xcode=ydbj&guid=3d877591-1456-4cdf-9abe-f821d17ef28f",
            beta = "http://dingding.oa.corpautohome.com/send?xcode=ydbj&guid=3d877591-1456-4cdf-9abe-f821d17ef28f",
            online = "http://dingding.oa.corpautohome.com/send?xcode=ydbj&guid=3d877591-1456-4cdf-9abe-f821d17ef28f",
            timeout = 3000
    )
    CompletableFuture<DingDingApiResult> sendDingDingMsg(@PostBody DingDingMessageParam param);
}
