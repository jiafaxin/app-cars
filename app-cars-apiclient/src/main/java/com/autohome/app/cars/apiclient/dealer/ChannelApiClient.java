package com.autohome.app.cars.apiclient.dealer;

import com.autohome.app.cars.apiclient.dealer.dtos.SpecChannelReq;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecChannelResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;
import com.autohome.app.cars.common.httpclient.annotation.AutoPost;
import com.autohome.app.cars.common.httpclient.annotation.PostBody;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/4/26
 */
@AutoHttpClient
public interface ChannelApiClient {

    /**
     * 车型-渠道查询接口
     *
     * 源接口：http://doc.phonebus.corpautohome.com/project/1121/interface/api/54210
     */
    @AutoPost(
            dev = "http://mdmserviceapi.lq.autohome.com.cn/Channel/getChannelInfoList",
            beta = "http://mdmserviceapi.lq.autohome.com.cn/Channel/getChannelInfoList",
            online = "http://mdmserviceapi.lq.autohome.com.cn/Channel/getChannelInfoList",
            timeout = 1000
    )
    CompletableFuture<SpecChannelResult> getChannelInfoList(@PostBody SpecChannelReq req);
}
