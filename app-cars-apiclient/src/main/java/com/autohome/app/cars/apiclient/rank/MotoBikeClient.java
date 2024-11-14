package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.MotoRankResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/7/18
 */
@AutoHttpClient
public interface MotoBikeClient {

    @AutoGet(
            dev = "http://uc-car-findcar.msapi.autohome.com.cn/motorbike/searchrank?levelid=${levelId}&pagesize=${pageSize}&pageindex=${pageIndex}",
            beta = "http://uc-car-findcar.msapi.autohome.com.cn/motorbike/searchrank?levelid=${levelId}&pagesize=${pageSize}&pageindex=${pageIndex}",
            online = "http://uc-car-findcar.msapi.autohome.com.cn/motorbike/searchrank?levelid=${levelId}&pagesize=${pageSize}&pageindex=${pageIndex}"
    )
    CompletableFuture<MotoRankResult> getMotoRankList(int levelId, int pageIndex, int pageSize);
}
