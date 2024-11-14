package com.autohome.app.cars.apiclient.abtest;

import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface AbApiClient {

    /**
     * wiki https://doc.autohome.com.cn/docapi/page/share/share_mlRrUZ1I6S
     *
     * @return
     */
    @AutoPost(
            dev = "http://abtest.serviceapi.corpautohome.com/abapi/test/listV2?appkey=automain&testids=${testids}&deviceid=${deviceid}",
            beta = "http://abtest.serviceapi.corpautohome.com/abapi/test/listV2?appkey=automain&testids=${testids}&deviceid=${deviceid}",
            online = "http://abtest.serviceapi.corpautohome.com/abapi/test/listV2?appkey=automain&testids=${testids}&deviceid=${deviceid}",
            timeout = 250
    )
    CompletableFuture<ABTestDto> getABTest(String testids, String deviceid);
}
