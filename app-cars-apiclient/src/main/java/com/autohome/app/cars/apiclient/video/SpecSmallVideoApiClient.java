package com.autohome.app.cars.apiclient.video;

import com.autohome.app.cars.apiclient.video.dtos.SpecShiCeSmallVideoResult;
import com.autohome.app.cars.apiclient.video.dtos.SpecSmallVideoResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface SpecSmallVideoApiClient {

    /**
     * 接口对接人：曹飞
     */
    @AutoGet(
            dev = "http://intranet.sv.api.autohome.com.cn/100/video/getbooklist_v1?_appid=app&businessid=62&series_id=0&spec_ids=${specIds}",
            beta = "http://intranet.sv.api.autohome.com.cn/100/video/getbooklist_v1?_appid=app&businessid=62&series_id=0&spec_ids=${specIds}",
            online = "http://intranet.sv.api.autohome.com.cn/100/video/getbooklist_v1?_appid=app&businessid=62&series_id=0&spec_ids=${specIds}",
            timeout = 1000
    )
    CompletableFuture<SpecSmallVideoResult> getConfigSmallVideoResult(String specIds);

    /**
     * 接口对接人：曹飞
     */
    @AutoGet(
            dev = "http://intranet.sv.api.autohome.com.cn/api/car/realtest/getbyspec?specids=${specIds}",
            beta = "http://intranet.sv.api.autohome.com.cn/api/car/realtest/getbyspec?specids=${specIds}",
            online = "http://intranet.sv.api.autohome.com.cn/api/car/realtest/getbyspec?specids=${specIds}",
            timeout = 1000
    )
    CompletableFuture<SpecShiCeSmallVideoResult> getShiCeSmallVideoResult(String specIds);

}
