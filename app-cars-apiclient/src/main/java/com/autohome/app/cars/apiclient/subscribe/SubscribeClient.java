package com.autohome.app.cars.apiclient.subscribe;

import com.autohome.app.cars.apiclient.subscribe.dtos.SubscribedSeriesDto;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 通过设备号查询用户最近订阅的50条数据
 *
 * @author zhangchengtao
 * @date 2024/9/5 15:27
 */
@AutoHttpClient
public interface SubscribeClient {
    @AutoGet(dev = "http://carsdevice.thallo.autohome.com.cn/carsdevice/subscribe/getSubscribeSeriesAndSpec?deviceId=${deviceId}",
            beta = "http://carsdevice.thallo.autohome.com.cn/carsdevice/subscribe/getSubscribeSeriesAndSpec?deviceId=${deviceId}",
            online = "http://carsdevice.corpautohome.com/carsdevice/subscribe/getSubscribeSeriesAndSpec?deviceId=${deviceId}")
    CompletableFuture<BaseModel<List<SubscribedSeriesDto>>> getSubscribeSeriesAndSpecList(String deviceId);
}
