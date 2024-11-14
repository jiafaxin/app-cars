package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.ClueCheckAllResultDto;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhangchengtao
 * @date 2024/4/30 11:18
 */
@AutoHttpClient
public interface ClueRepeatClient {
    @AutoGet(
            dev = "http://carsdevice.corpautohome.com/carsdevice/check/getinfoasked?cityid=${cityid}&deviceid=${deviceid}&noCache=noCache",
            beta = "http://carsdevice.corpautohome.com/carsdevice/check/getinfoasked?cityid=${cityid}&deviceid=${deviceid}&noCache=noCache",
            online = "http://carsdevice.corpautohome.com/carsdevice/check/getinfoasked?cityid=${cityid}&deviceid=${deviceid}&noCache=noCache"
    )
    CompletableFuture<ClueCheckAllResultDto> getCheckAllResult(int cityid, String deviceid);
}
